package ru.vladsa.wordteacher.dictionaries;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class DictionaryData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "title")
    private String name;

    @ColumnInfo(name = "count")
    private long wordCount;

    @ColumnInfo(name = "value")
    private boolean value;

    public DictionaryData(String name, long wordCount, boolean value) {
        this.id = 0;
        this.name = name;
        this.wordCount = wordCount;
        this.value = value;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public long getWordCount() {
        return wordCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryData that = (DictionaryData) o;
        return value == that.value && wordCount == that.wordCount && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, wordCount);
    }

    @NonNull
    @Override
    public String toString() {
        return "DictionaryData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wordCount=" + wordCount +
                ", value=" + value +
                '}';
    }
}
