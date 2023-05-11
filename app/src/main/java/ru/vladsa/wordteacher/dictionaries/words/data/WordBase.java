package ru.vladsa.wordteacher.dictionaries.words.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.words.WordData;

@Database(entities = {WordData.class}, version = 1)
public abstract class WordBase extends RoomDatabase {
    public abstract WordDao wordDao();
}
