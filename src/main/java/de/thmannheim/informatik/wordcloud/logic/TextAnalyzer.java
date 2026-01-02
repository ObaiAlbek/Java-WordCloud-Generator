package de.thmannheim.informatik.wordcloud.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class TextAnalyzer {

    /**
     * Zerlegt einen Text in einzelne Wörter (Tokens), entfernt Stoppwörter,
     * führt Stemming durch und filtert Zahlen/kurze Wörter raus.
     * * @param text Der Rohtext
     * @return Liste der verarbeiteten Wörter
     */
    public List<String> analyze(String text) {
        List<String> words = new ArrayList<>();
        
        // Der GermanAnalyzer hat eingebaute deutsche Stoppwörter und Stemming-Regeln
        try (Analyzer analyzer = new GermanAnalyzer()) {
            TokenStream tokenStream = analyzer.tokenStream(null, text);
            
            // Wir wollen an den Text des jeweiligen Tokens (Wortes) kommen
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String word = attr.toString();
                
                // --- FILTER LOGIK ---
                // 1. Wort muss mindestens 2 Buchstaben haben
                // 2. Wort darf keine Ziffern enthalten (0-9) -> RegEx ".*\\d.*" prüft auf Ziffern
                if (word.length() < 2 || word.matches(".*\\d.*")) {
                    continue; // Überspringen
                }
                
                // Wenn alles okay ist, hinzufügen
                words.add(word);
            }
            tokenStream.end();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return words;
    }
}