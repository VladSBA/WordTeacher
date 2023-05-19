package ru.vladsa.wordteacher.dictionaries.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;

@Dao
public interface DictionaryDao {
    @Query("SELECT * FROM DictionaryData")
    List<DictionaryData> getAll();

    @Query("SELECT * FROM DictionaryData WHERE title like:search")
    List<DictionaryData> getFromLike(String search);

    @Query("SELECT * FROM DictionaryData WHERE id like:id")
    List<DictionaryData> getFromId(long id);

    @Query("SELECT * FROM DictionaryData WHERE value like:value")
    List<DictionaryData> getFromValue(String value);

    @Insert
    void insertAll(DictionaryData... dictionary);

    @Delete
    void delete(DictionaryData dictionary);

    @Update
    void update(DictionaryData dictionary);
}
