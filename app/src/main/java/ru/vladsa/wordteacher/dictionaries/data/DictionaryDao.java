package ru.vladsa.wordteacher.dictionaries.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Dictionary;
import java.util.List;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;

@Dao
public interface DictionaryDao {
    @Query("SELECT * FROM DictionaryData")
    List<DictionaryData> getAll();

    @Query("SELECT * FROM DictionaryData WHERE title like:search")
    List<DictionaryData> getFromLike(String search);

    @Insert
    void insertAll(DictionaryData... dictionary);

    @Delete
    void delete(DictionaryData dictionary);

    @Update
    void update(DictionaryData dictionary);
}
