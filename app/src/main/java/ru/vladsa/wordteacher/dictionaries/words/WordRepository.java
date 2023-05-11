package ru.vladsa.wordteacher.dictionaries.words;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.vladsa.wordteacher.dictionaries.words.data.WordBase;

public class WordRepository {

    private static WordRepository instance = null;
    private WordBase roomdb;

    public static WordRepository getInstance(Context context) {
        if (instance == null) instance = new WordRepository(context);
        return instance;
    }

    private final ArrayList<WordData> words = new ArrayList<>();

    public WordRepository(Context context) {
        roomdb = Room.databaseBuilder(context, WordBase.class, "database-name").allowMainThreadQueries().build();
    }

    public ArrayList<WordData> getWords() {
        return words;
    }

    public void addWord(WordData wordData) {
        words.clear();
        roomdb.wordDao().insertAll(wordData);
        words.addAll(roomdb.wordDao().getAll());
        words.add(wordData);

    }

    public void addWords(List<WordData> wordData) {
        words.clear();
        roomdb.wordDao().insertAll((WordData[]) wordData.toArray());
        words.addAll(roomdb.wordDao().getAll());
        words.addAll(wordData);

    }
}
