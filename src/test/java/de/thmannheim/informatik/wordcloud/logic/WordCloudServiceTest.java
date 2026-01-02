package de.thmannheim.informatik.wordcloud.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.thmannheim.informatik.wordcloud.model.Word;

class WordCloudServiceTest {

    private WordCloudService service;

    @TempDir
    Path tempDir; // JUnit erstellt hier einen temporären Ordner für uns

    @BeforeEach
    void setUp() {
        service = new WordCloudService();
    }

    @Test
    @DisplayName("Sollte Textdatei lesen und korrekt zählen")
    void testProcessTxtFile() throws IOException {
        // ARRANGE: Wir erstellen eine echte Test-Datei im temporären Ordner
        File testFile = tempDir.resolve("test.txt").toFile();
        String content = "Java Java Java Python Python C++";
        Files.writeString(testFile.toPath(), content);

        // ACT: Service aufrufen (Min Freq 1, Max Words 10, keine Custom Stopwords)
        List<Word> result = service.processSource(testFile, 1, 10, null, null);

        
        // Das häufigste Wort muss Java sein (3 mal)
        assertEquals("java", result.get(0).getText());
        assertEquals(3, result.get(0).getFrequency());
    }

    @Test
    @DisplayName("Sollte Min-Frequency Filter beachten")
    void testMinFrequencyFilter() throws IOException {
        // ARRANGE
        File testFile = tempDir.resolve("filter.txt").toFile();
        // 'wichtig' kommt 3x vor, 'egal' nur 1x
        String content = "wichtig wichtig wichtig egal";
        Files.writeString(testFile.toPath(), content);

        // ACT: Min Frequency auf 2 setzen
        List<Word> result = service.processSource(testFile, 2, 10, null, null);

        // ASSERT
        assertEquals(1, result.size(), "Nur 'wichtig' sollte übrig bleiben");
        assertEquals("wichtig", result.get(0).getText());
    }

    @Test
    @DisplayName("Sollte eigene Stoppwörter ignorieren")
    void testCustomStopwords() throws IOException {
        // ARRANGE
        File testFile = tempDir.resolve("stop.txt").toFile();
        String content = "Haus Garten Baum";
        Files.writeString(testFile.toPath(), content);

        // ACT: Wir sagen, dass 'Garten' ignoriert werden soll
        String customStopwords = "garten, baum";
        List<Word> result = service.processSource(testFile, 1, 10, customStopwords, null);

        // ASSERT
        assertEquals("haus", result.get(0).getText());
    }
}