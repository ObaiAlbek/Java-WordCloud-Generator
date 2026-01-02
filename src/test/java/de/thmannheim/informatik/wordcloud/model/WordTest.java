package de.thmannheim.informatik.wordcloud.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WordTest {

    @Test
    void testCompareTo() {
        Word frequentWord = new Word("test", 10);
        Word rareWord = new Word("selten", 1);

        // compareTo < 0 bedeutet: frequentWord kommt VOR rareWord in der Liste (absteigend)
        // compareTo > 0 bedeutet: frequentWord kommt NACH rareWord
        
        // Da wir absteigend sortieren wollen (Häufigstes zuerst), 
        // muss bei Collections.sort() das Ergebnis entsprechend sein.
        // In deiner Implementierung: other.freq - this.freq
        // 1 - 10 = -9
        
        assertTrue(frequentWord.compareTo(rareWord) < 0, 
                "Wörter mit höherer Frequenz sollten beim Sortieren zuerst kommen");
    }
    
    @Test
    void testIncrement() {
        Word w = new Word("test", 1);
        w.incrementFrequency();
        assertEquals(2, w.getFrequency(), "Frequenz sollte sich erhöhen");
    }
}