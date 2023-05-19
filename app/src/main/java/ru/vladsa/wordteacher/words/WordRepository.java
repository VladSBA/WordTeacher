package ru.vladsa.wordteacher.words;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.words.data.WordBase;

public class WordRepository {

    private static WordRepository instance = null;
    private final WordBase roomdb;

    public static WordRepository getInstance(Context context) {
        if (instance == null) instance = new WordRepository(context);
        return instance;
    }

    private final ArrayList<WordData> words = new ArrayList<>();

    public WordRepository(Context context) {
        roomdb = Room.databaseBuilder(context, WordBase.class, "database-word-name").allowMainThreadQueries().build();
    }

    public List<WordData> getWords() {
        return words;
    }

    public List<WordData> getDictionaryWords(long search){
        words.clear();
        words.addAll(roomdb.wordDao().getFromDictionary(search));
        return words;
    }

    public int getAllWordCount() {
        words.clear();
        words.addAll(roomdb.wordDao().getAll());
        return words.size();
    }

    public List<WordData> getLike(String search){
        words.clear();
        words.addAll(roomdb.wordDao().getFromLike(search));
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

    public List<WordData> getWordsFromId(long id) {
        words.clear();
        words.addAll(roomdb.wordDao().getWordsFromId(id));
        return words;
    }

    public List<WordData> getWordsFromDictionary(long id) {
        words.clear();
        words.addAll(roomdb.wordDao().getFromDictionary(id));
        return words;
    }


    public void removeByPosition(WordData word) {
        roomdb.wordDao().delete(word);
        words.remove(word);

    }

    public void updateWord(WordData temp) {
        roomdb.wordDao().update(temp);
    }

}
