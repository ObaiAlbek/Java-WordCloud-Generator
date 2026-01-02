package de.thmannheim.informatik.wordcloud.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocxReader implements DocumentReader {

    @Override
    public String readText(File file) throws IOException {
        // Wir nutzen FileInputStream, um die Datei zu laden
        try (FileInputStream fis = new FileInputStream(file);
             // XWPFDocument ist das Format für .docx Dateien
             XWPFDocument document = new XWPFDocument(fis);
             // Der Extractor zieht den reinen Text aus dem Dokument
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            
            return extractor.getText();
        }
    }
}