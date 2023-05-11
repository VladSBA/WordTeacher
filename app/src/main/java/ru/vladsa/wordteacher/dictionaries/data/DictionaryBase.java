package ru.vladsa.wordteacher.dictionaries.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;

@Database(entities = {DictionaryData.class}, version = 1)
public abstract class DictionaryBase extends RoomDatabase {
    public abstract DictionaryDao dictionaryDao();
}
