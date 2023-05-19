package ru.vladsa.wordteacher.dictionaries;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.dictionaries.data.DictionaryBase;

public class DictionaryRepository {

    private static DictionaryRepository instance = null;
    private final DictionaryBase roomdb;

    public static DictionaryRepository getInstance(Context context) {
        if (instance == null) instance = new DictionaryRepository(context);
        return instance;
    }

    private final ArrayList<DictionaryData> dictionaries = new ArrayList<>();

    public DictionaryRepository(Context context) {
        roomdb = Room.databaseBuilder(context, DictionaryBase.class, "database-dictionary-name").allowMainThreadQueries().build();
    }

    public int getAllDictionariesCount() {
        dictionaries.clear();
        dictionaries.addAll(roomdb.dictionaryDao().getAll());
        return dictionaries.size();
    }

    public List<DictionaryData> getFromId(long id) {
        dictionaries.clear();
        dictionaries.addAll(roomdb.dictionaryDao().getFromId(id));
        return dictionaries;
    }

    public List<DictionaryData> getFromValue(String value) {
        dictionaries.clear();
        dictionaries.addAll(roomdb.dictionaryDao().getFromValue(value));
        return dictionaries;
    }

    public ArrayList<DictionaryData> getDictionaries() {
        dictionaries.clear();
        dictionaries.addAll(roomdb.dictionaryDao().getAll());
        return dictionaries;
    }

    public void addDictionary(DictionaryData dictionaryData) {
        dictionaries.clear();
        roomdb.dictionaryDao().insertAll(dictionaryData);
        dictionaries.addAll(roomdb.dictionaryDao().getAll());
        dictionaries.add(dictionaryData);

    }

    public void removeByPosition(DictionaryData dictionary) {
        roomdb.dictionaryDao().delete(dictionary);
        dictionaries.remove(dictionary);
    }

    public void updateDictionary(DictionaryData temp) {
        roomdb.dictionaryDao().update(temp);
    }

}
