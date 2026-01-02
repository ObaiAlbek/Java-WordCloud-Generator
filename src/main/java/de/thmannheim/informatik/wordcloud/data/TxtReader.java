package de.thmannheim.informatik.wordcloud.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TxtReader implements DocumentReader {

    @Override
    public String readText(File file) throws IOException {
        // Liest alle Bytes der Datei und wandelt sie in einen String um
        return new String(Files.readAllBytes(file.toPath()));
    }
}