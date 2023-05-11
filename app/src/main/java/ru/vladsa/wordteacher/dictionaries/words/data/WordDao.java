package ru.vladsa.wordteacher.dictionaries.words.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.words.WordData;

@Dao
public interface WordDao {
    @Query("SELECT * FROM WordData")
    List<WordData> getAll();

    @Query("SELECT * FROM WordData WHERE word like:search or meaning like :search or dictionary like :search")
    List<WordData> getFromLike(String search);

    @Query("SELECT * FROM WordData WHERE image like :image")
    List<WordData> findImageUsage(String image);

    @Insert
    void insertAll(WordData... wordData);

    @Delete
    void delete(WordData wordData);

    @Update
    void update(WordData wordData);
}