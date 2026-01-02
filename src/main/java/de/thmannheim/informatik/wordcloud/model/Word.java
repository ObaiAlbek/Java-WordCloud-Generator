package de.thmannheim.informatik.wordcloud.model;

public class Word implements Comparable<Word> {
    private String text;
    private int frequency;

    public Word(String text, int frequency) {
        this.text = text;
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    public String getText() {
        return text;
    }

    public int getFrequency() {
        return frequency;
    }

    // Damit wir später Listen von Wörtern sortieren können (häufigste zuerst)
    @Override
    public int compareTo(Word other) {
        // Absteigend sortieren (hohe Frequenz zuerst)
        return Integer.compare(other.frequency, this.frequency);
    }
    
    @Override
    public String toString() {
        return text + " (" + frequency + ")";
    }
}