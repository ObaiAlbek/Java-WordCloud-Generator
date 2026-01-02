package de.thmannheim.informatik.wordcloud.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.thmannheim.informatik.wordcloud.model.Word;

public class HtmlGenerator {

    /**
     * Erstellt eine HTML-Datei mit der Wordcloud.
     * @param words Die Liste der Wörter
     * @param outputFile Die Zieldatei (z.B. cloud.html)
     */
    public void generateHtml(List<Word> words, File outputFile) {
        StringBuilder html = new StringBuilder();

        // 1. HTML Header & CSS (damit es schön aussieht)
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>WordCloud Ergebnis</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; text-align: center; background-color: #f9f9f9; }\n");
        html.append("h1 { color: #333; }\n");
        html.append(".cloud { display: flex; flex-wrap: wrap; justify-content: center; align-items: center; max-width: 900px; margin: 20px auto; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n");
        html.append(".word { margin: 5px 10px; color: #0078d7; text-decoration: none; transition: all 0.3s ease; line-height: 1.2; }\n");
        // Hover Effekt: Farbe ändern und leicht vergrößern
        html.append(".word:hover { color: #e81123; transform: scale(1.1); text-shadow: 2px 2px 5px rgba(0,0,0,0.2); cursor: pointer; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        html.append("<h1>WordCloud</h1>\n");
        html.append("<p>Erstelltes Ergebnis aus deiner Datei</p>\n");
        html.append("<div class='cloud'>\n");

        // 2. Wörter generieren
        // Wir müssen die Schriftgröße berechnen. 
        if (!words.isEmpty()) {
            int maxFreq = words.get(0).getFrequency(); // Das erste ist das häufigste (da sortiert)
            int minFreq = words.get(words.size() - 1).getFrequency();

            for (Word w : words) {
                int size = calculateFontSize(w.getFrequency(), minFreq, maxFreq);
                
                // Link zur Google Suche bauen (Aufgabe: "Link-Vorgabe soll gesetzt werden können")
                // Hier als Standard Google Suche
                String googleLink = "https://www.google.com/search?q=" + w.getText();
                
                html.append(String.format(
                    "<a href='%s' target='_blank' class='word' style='font-size: %dpx' title='%d Vorkommen'>%s</a>\n",
                    googleLink, size, w.getFrequency(), w.getText()
                ));
            }
        } else {
            html.append("<p>Keine Wörter gefunden (Prüfe die Filter-Einstellungen).</p>");
        }

        // 3. Footer
        html.append("</div>\n");
        html.append("<p style='color: #888; font-size: 12px; margin-top: 30px;'>Generiert mit Java WordCloudGenerator</p>\n");
        html.append("</body>\n</html>");

        // 4. Datei schreiben
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(html.toString());
            System.out.println("HTML erstellt: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Berechnet eine Schriftgröße zwischen 15 und 80 Pixeln, je nach Häufigkeit.
     */
    private int calculateFontSize(int freq, int min, int max) {
        if (max == min) return 40; // Falls alle gleich oft vorkommen
        int minSize = 15;
        int maxSize = 80;
        
        // Lineare Interpolation
        return minSize + (freq - min) * (maxSize - minSize) / (max - min);
    }
}