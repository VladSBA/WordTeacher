package ru.vladsa.wordteacher.dictionaries;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;

import ru.vladsa.wordteacher.dictionaries.data.DictionaryBase;

public class DictionaryRepository {

    private static DictionaryRepository instance = null;
    private DictionaryBase roomdb;

    public static DictionaryRepository getInstance(Context context) {
        if (instance == null) instance = new DictionaryRepository(context);
        return instance;
    }

    private final ArrayList<DictionaryData> dictionaries = new ArrayList<>();

    public DictionaryRepository(Context context) {
        roomdb = Room.databaseBuilder(context, DictionaryBase.class, "database-name").allowMainThreadQueries().build();
    }

    public ArrayList<DictionaryData> getDictionaries() {
        return dictionaries;
    }

    public void addDictionary(DictionaryData dictionaryData) {
        dictionaries.clear();
        roomdb.dictionaryDao().insertAll(dictionaryData);
        dictionaries.addAll(roomdb.dictionaryDao().getAll());
        dictionaries.add(dictionaryData);

    }
}
