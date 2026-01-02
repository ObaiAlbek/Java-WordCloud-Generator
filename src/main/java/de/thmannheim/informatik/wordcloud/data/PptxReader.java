package de.thmannheim.informatik.wordcloud.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class PptxReader implements DocumentReader {

    @Override
    public String readText(File file) throws IOException {
        StringBuilder sb = new StringBuilder();

        // XMLSlideShow ist das Format für .pptx
        try (FileInputStream fis = new FileInputStream(file);
             XMLSlideShow ppt = new XMLSlideShow(fis)) {

            // Durch alle Folien (Slides) gehen
            for (XSLFSlide slide : ppt.getSlides()) {
                // Auf jeder Folie alle Elemente (Shapes) durchsuchen
                for (XSLFShape shape : slide.getShapes()) {
                    // Ist das Element ein Textfeld?
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        sb.append(textShape.getText());
                        sb.append(" "); // Leerzeichen nach jedem Block
                    }
                }
            }
        }
        return sb.toString();
    }
}