package de.thmannheim.informatik.wordcloud.data;

import java.io.File;
import java.io.IOException;

public interface DocumentReader {
    /**
     * Liest den Textinhalt aus einer Datei.
     * @param file Die zu lesende Datei
     * @return Der extrahierte Text als String
     * @throws IOException Wenn beim Lesen ein Fehler auftritt
     */
    String readText(File file) throws IOException;
}