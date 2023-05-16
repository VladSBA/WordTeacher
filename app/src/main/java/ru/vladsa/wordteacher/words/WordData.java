package ru.vladsa.wordteacher.words;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class WordData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "meaning")
    private String meaning;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "dictionary")
    private long dictionaryID;

    public WordData(String word, String meaning, String image, long dictionaryID) {
        this.id = 0;
        this.word = word;
        this.meaning = meaning;
        this.image = image;
        this.dictionaryID = dictionaryID;
    }

    public long getDictionaryID() {
        return dictionaryID;
    }

    public void setDictionaryID(long dictionaryID) {
        this.dictionaryID = dictionaryID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordData that = (WordData) o;
        return image.equals(that.image) && Objects.equals(word, that.word) && Objects.equals(meaning, that.meaning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, meaning, image);
    }

    @NonNull
    @Override
    public String toString() {
        return "WordData{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                ", image='" + image + '\'' +
                ", dictionaryID=" + dictionaryID +
                '}';
    }
}
