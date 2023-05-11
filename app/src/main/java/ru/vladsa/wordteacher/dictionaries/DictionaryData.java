package ru.vladsa.wordteacher.dictionaries;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class DictionaryData {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "title")
    private String name;

    @ColumnInfo(name = "value")
    private boolean value;

    public DictionaryData(String name, boolean value) {
        this.id = 0;
        this.name = name;
        this.value = value;
    }

    public int getWordCount() {
        //TODO: getWordCount
        return 0;
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
        return value == that.value && getWordCount() == that.getWordCount() && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, getWordCount());
    }
}
