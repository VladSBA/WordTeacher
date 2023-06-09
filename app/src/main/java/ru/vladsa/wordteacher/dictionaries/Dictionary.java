package ru.vladsa.wordteacher.dictionaries;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.MainActivity;
import ru.vladsa.wordteacher.words.Word;

public class Dictionary implements Serializable {

    private static final String LOG_TAG = MainActivity.LOG_TAG + "_Dictionary";

    private String name;
    private List<Word> words;

    public Dictionary(String name, List<Word> words) {
        this.name = name;
        this.words = new ArrayList<>();
        this.words.addAll(words);
    }

    public int getWordCount() {
        return words.size();
    }

    public boolean save(ObjectOutputStream oos) {
        Log.d(LOG_TAG, String.format("Saving dictionary %s... to %s", this, oos));

        try {
            oos.writeObject(this);
            oos.flush();

            Log.d(LOG_TAG, "Dictionary has saved.");

            return true;

        } catch (IOException ex) {
            Log.e(LOG_TAG, "Dictionary has not saved.");
            ex.printStackTrace();

            return false;
        }
    }

    public static Dictionary load(ObjectInputStream ois) {
        Log.d(LOG_TAG, "Loading dictionary...");

        try {
            Dictionary dictionary = (Dictionary) ois.readObject();

            Log.d(LOG_TAG, String.format("Dictionary %s has read.", dictionary));

            return dictionary;
        } catch (IOException | ClassNotFoundException ex) {
            Log.e(LOG_TAG, "Dictionary has not read.");
            Log.e(LOG_TAG, ex.getMessage());
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    @NonNull
    @Override
    public String toString() {
        return "Dictionary{" +
                "name='" + name + '\'' +
                ", words=" + words +
                '}';
    }
}
