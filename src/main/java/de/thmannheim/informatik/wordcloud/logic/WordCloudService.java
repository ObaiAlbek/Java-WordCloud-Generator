package de.thmannheim.informatik.wordcloud.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer; // <--- WICHTIG: Importieren!

import de.thmannheim.informatik.wordcloud.data.*; // Deine Reader Imports
import de.thmannheim.informatik.wordcloud.model.Word;

public class WordCloudService {

    private TextAnalyzer analyzer = new TextAnalyzer();

    /**
     * Verarbeitet eine Datei oder einen Ordner mit Status-Updates.
     * @param onProgressCallback Eine Funktion, die aufgerufen wird, um Status zu melden (z.B. an die GUI)
     */
    public List<Word> processSource(File source, int minFrequency, int maxWords, String customStopwordsString, Consumer<String> onProgressCallback) {
        Map<String, Integer> totalWordCounts = new HashMap<>();
        
        // 1. Alle Dateien sammeln
        if (onProgressCallback != null) onProgressCallback.accept("Suche Dateien in " + source.getName() + "...");
        List<File> filesToProcess = new ArrayList<>();
        collectFiles(source, filesToProcess);
        
        if (onProgressCallback != null) onProgressCallback.accept("Gefundene Dateien: " + filesToProcess.size());

        // 2. Stopwords vorbereiten
        Set<String> customStopwords = new HashSet<>();
        if (customStopwordsString != null && !customStopwordsString.isEmpty()) {
            String[] split = customStopwordsString.split("[,\\s]+");
            for (String s : split) customStopwords.add(s.toLowerCase().trim());
        }

        // 3. Verarbeiten
        int count = 0;
        for (File file : filesToProcess) {
            count++;
            // Hier senden wir die Nachricht an die GUI:
            if (onProgressCallback != null) {
                onProgressCallback.accept("Lese (" + count + "/" + filesToProcess.size() + "): " + file.getName());
            }

            try {
                DocumentReader reader = getReaderForFile(file);
                if (reader == null) continue;

                String text = reader.readText(file);
                List<String> tokens = analyzer.analyze(text);

                for (String token : tokens) {
                    if (customStopwords.contains(token.toLowerCase())) continue; 
                    totalWordCounts.put(token, totalWordCounts.getOrDefault(token, 0) + 1);
                }

            } catch (Exception e) {
                System.err.println("Fehler bei " + file.getName());
            }
        }

        // 4. Ergebnis bauen
        if (onProgressCallback != null) onProgressCallback.accept("Erstelle Statistik...");
        List<Word> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : totalWordCounts.entrySet()) {
            if (entry.getValue() >= minFrequency) {
                result.add(new Word(entry.getKey(), entry.getValue()));
            }
        }

        Collections.sort(result);
        if (result.size() > maxWords) {
            return result.subList(0, maxWords);
        }
        
        return result;
    }

    private void collectFiles(File node, List<File> list) {
        if (node.isFile()) {
            if (getReaderForFile(node) != null) list.add(node);
        } else if (node.isDirectory()) {
            File[] children = node.listFiles();
            if (children != null) {
                for (File child : children) collectFiles(child, list);
            }
        }
    }

    private DocumentReader getReaderForFile(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".txt")) return new TxtReader();
        if (name.endsWith(".pdf")) return new PdfReader();
        if (name.endsWith(".docx")) return new DocxReader();
        if (name.endsWith(".pptx")) return new PptxReader();
        return null;
    }
}