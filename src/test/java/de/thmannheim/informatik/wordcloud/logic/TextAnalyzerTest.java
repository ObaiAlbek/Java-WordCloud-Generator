package de.thmannheim.informatik.wordcloud.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextAnalyzerTest {

    private TextAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new TextAnalyzer();
    }

    @Test
    @DisplayName("Sollte einfache Sätze in Tokens zerlegen")
    void testBasicTokenization() {
        String input = "Hallo Welt";
        List<String> result = analyzer.analyze(input);
        
        // "hallo" ist oft ein Stoppwort oder wird kleingeschrieben, "welt" bleibt
        assertTrue(result.contains("welt"), "Das Wort 'welt' sollte gefunden werden");
    }

    @Test
    @DisplayName("Sollte Zahlen und kurze Wörter (<2 Zeichen) entfernen")
    void testFilterNumbersAndShortWords() {
        String input = "Das ist 1 Test mit 123 und a b c";
        List<String> result = analyzer.analyze(input);

        assertFalse(result.contains("1"), "Einzelne Zahlen sollten entfernt werden");
        assertFalse(result.contains("123"), "Zahlenfolgen sollten entfernt werden");
        assertFalse(result.contains("a"), "Einzelne Buchstaben sollten entfernt werden");
    }

    @Test
    @DisplayName("Sollte Stemming durchführen (Grundformen finden)")
    void testStemming() {
        // "Häuser" -> "haus", "laufend" -> "lauf"
        String input = "Die Häuser sind laufend";
        List<String> result = analyzer.analyze(input);

        assertTrue(result.contains("haus"), "Häuser sollte zu 'haus' gestemmt werden");
    }
    
    @Test
    @DisplayName("Sollte Stoppwörter entfernen")
    void testStopWords() {
        String input = "Ich bin der und die oder das";
        List<String> result = analyzer.analyze(input);
        
        // Diese Wörter sollten vom GermanAnalyzer alle gefiltert werden
        assertTrue(result.isEmpty(), "Standard-Stoppwörter sollten entfernt werden");
    }
}