package de.thmannheim.informatik.wordcloud.data;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfReader implements DocumentReader {

    @Override
    public String readText(File file) throws IOException {
        // PDF laden (nutzt die neue Loader-Klasse aus PDFBox 3.x)
        try (PDDocument document = Loader.loadPDF(file)) {
            // Text extrahieren
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}