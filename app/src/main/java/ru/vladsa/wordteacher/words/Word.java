package ru.vladsa.wordteacher.words;

import static ru.vladsa.wordteacher.DictionaryEditActivity.GETTING_IMAGE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class Word implements Serializable {
    private String word;
    private String meaning;
    private Bitmap image;

    public Word(String word, String meaning, Bitmap image) {
        this.word = word;
        this.meaning = meaning;
        this.image = image;
    }

    public Word(WordData word) {
        this.word = word.getWord();
        meaning = word.getMeaning();

        if (word.getImage() != null && !word.getImage().equals("null") && !word.getImage().isEmpty() && !word.getImage().equals(GETTING_IMAGE)) {

            try {
                FileInputStream fis = new FileInputStream(word.getImage());
                image = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else {
            image = null;
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return "Word{" +
                "name='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                ", image " + image != null ? "exists" : "not exists" +
                '}';
    }

}
