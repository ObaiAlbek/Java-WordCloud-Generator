package de.thmannheim.informatik.wordcloud.main;

import de.thmannheim.informatik.wordcloud.app.WordCloudApp;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Startet unsere WordCloudApp (die GUI)
        Application.launch(WordCloudApp.class, args);
    }
}