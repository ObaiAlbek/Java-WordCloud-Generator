package de.thmannheim.informatik.wordcloud.app;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

import de.thmannheim.informatik.wordcloud.logic.HtmlGenerator;
import de.thmannheim.informatik.wordcloud.logic.WordCloudService;
import de.thmannheim.informatik.wordcloud.model.Word;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WordCloudApp extends Application {

    private WordCloudService service = new WordCloudService();
    
    // UI Elemente
    private TextArea resultArea;
    private TextField maxWordsField;
    private TextField minFreqField;
    private TextArea stopWordsArea;
    private Label statusLabel; // <-- NEU: Das Label für unten
    private Stage primaryStage;
    
    // Buttons sperren während des Ladens
    private Button fileBtn;
    private Button folderBtn;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- LINKE SEITE (Optionen) ---
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(10));
        optionsBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        optionsBox.setPrefWidth(250);

        Label lblTitle = new Label("Optionen");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblMax = new Label("Max. Anzahl Wörter:");
        maxWordsField = new TextField("50");

        Label lblMin = new Label("Min. Häufigkeit:");
        minFreqField = new TextField("1");

        Label lblStop = new Label("Ignoriere diese Wörter:");
        stopWordsArea = new TextArea();
        stopWordsArea.setPromptText("z.B. hallo, welt");
        stopWordsArea.setPrefHeight(80);
        stopWordsArea.setWrapText(true);

        Label lblAction = new Label("Aktion wählen:");
        lblAction.setStyle("-fx-font-weight: bold; margin-top: 10px;");
        
        fileBtn = new Button("Einzelne Datei laden...");
        fileBtn.setMaxWidth(Double.MAX_VALUE);
        fileBtn.setOnAction(e -> chooseFile());
        
        folderBtn = new Button("Ganzen Ordner laden...");
        folderBtn.setMaxWidth(Double.MAX_VALUE);
        folderBtn.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;");
        folderBtn.setOnAction(e -> chooseFolder());

        optionsBox.getChildren().addAll(
                lblTitle, new Separator(),
                lblMax, maxWordsField,
                lblMin, minFreqField,
                lblStop, stopWordsArea,
                new Separator(),
                lblAction, fileBtn, folderBtn
        );
        root.setLeft(optionsBox);

        // --- MITTE (Ergebnis) ---
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setStyle("-fx-font-family: 'Consolas', monospace;");
        root.setCenter(resultArea);

        // --- UNTEN (Statusleiste) --- NEU!
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(5));
        bottomBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #aaa;");
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Bereit.");
        statusLabel.setStyle("-fx-text-fill: #333;");
        bottomBox.getChildren().add(statusLabel);
        
        root.setBottom(bottomBox);

        // --- Fenster ---
        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("WordCloud Generator v2.1 (Live Status)");
        stage.setScene(scene);
        stage.show();
    }

    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Datei wählen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dokumente", "*.txt", "*.pdf", "*.docx", "*.pptx"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) startProcessing(file);
    }
    
    private void chooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Ordner wählen");
        File folder = chooser.showDialog(primaryStage);
        if (folder != null) startProcessing(folder);
    }

    private void startProcessing(File source) {
        // UI vorbereiten
        resultArea.setText("");
        fileBtn.setDisable(true);
        folderBtn.setDisable(true);
        
        // Werte auslesen (muss im UI Thread passieren)
        int maxWords;
        int minFreq;
        String stopWords = stopWordsArea.getText();
        
        try {
            maxWords = Integer.parseInt(maxWordsField.getText());
            minFreq = Integer.parseInt(minFreqField.getText());
        } catch (NumberFormatException e) {
            statusLabel.setText("Fehler: Bitte gültige Zahlen eingeben!");
            fileBtn.setDisable(false);
            folderBtn.setDisable(false);
            return;
        }

        // --- BACKGROUND THREAD STARTEN ---
        // Damit die GUI nicht einfriert, machen wir die Arbeit in einem neuen Thread
        new Thread(() -> {
            
            try {
                // Hier übergeben wir den Callback (lambda), der das Label updated
                List<Word> words = service.processSource(source, minFreq, maxWords, stopWords, 
                    statusMessage -> {
                        // Da wir im Hintergrund-Thread sind, müssen wir UI-Updates mit Platform.runLater machen
                        Platform.runLater(() -> statusLabel.setText(statusMessage));
                    }
                );

                // Wenn fertig, Ergebnis im UI-Thread anzeigen
                Platform.runLater(() -> showResult(source, words));

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Fehler aufgetreten: " + e.getMessage());
                    resultArea.setText("Kritischer Fehler: " + e.getMessage());
                });
                e.printStackTrace();
            } finally {
                // Buttons wieder freigeben
                Platform.runLater(() -> {
                    fileBtn.setDisable(false);
                    folderBtn.setDisable(false);
                });
            }
            
        }).start();
    }
    
    private void showResult(File source, List<Word> words) {
        statusLabel.setText("Fertig! " + words.size() + " Wörter gefunden.");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Ergebnis für: ").append(source.getAbsolutePath()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-25s | %s\n", "WORT", "HÄUFIGKEIT")); 
        sb.append("--------------------------------------------------\n");
        
        for (Word w : words) {
            sb.append(String.format("%-25s | %d\n", w.getText(), w.getFrequency()));
        }
        
        if (words.isEmpty()) {
            sb.append("\nKeine Wörter gefunden.");
        } else {
            generateAndOpenHtml(words);
        }

        resultArea.setText(sb.toString());
    }

    private void generateAndOpenHtml(List<Word> words) {
        try {
            HtmlGenerator generator = new HtmlGenerator();
            File htmlFile = new File("wordcloud.html");
            generator.generateHtml(words, htmlFile);
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}