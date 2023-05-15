package ru.vladsa.wordteacher.words.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import ru.vladsa.wordteacher.words.WordData;

public class WordDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "word.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "words";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_MEANING = "meaning";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DICTIONARY = "dictionary";


    private static final long NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_WORD = 1;
    private static final int NUM_COLUMN_MEANING = 2;
    private static final int NUM_COLUMN_IMAGE = 3;
    private static final int NUM_COLUMN_DICTIONARY = 4;

    SQLiteDatabase database;

    public WordDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        database = getWritableDatabase();
    }


    public void add(WordData data) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WORD, data.getWord());
        cv.put(COLUMN_MEANING, data.getMeaning());
        cv.put(COLUMN_DICTIONARY, data.getDictionaryID());
        database.insert(TABLE_NAME, null, cv);
    }

    public ArrayList<WordData> getAll() {
        ArrayList<WordData> result = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.isAfterLast()) return new ArrayList<>();
        cursor.moveToFirst();
        do {
            result.add(new WordData(
                    cursor.getString(NUM_COLUMN_WORD),
                    cursor.getString(NUM_COLUMN_MEANING),
                    cursor.getString(NUM_COLUMN_IMAGE),
                    cursor.getLong(NUM_COLUMN_DICTIONARY)
            ));
        } while (cursor.moveToNext());
        cursor.close();
        return result;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_MEANING + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_DICTIONARY + " INTEGER); ";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void deleteDictionary(WordData data) {
        Log.d("DeleteSQLData", " " + data.getWord() + " " + database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(data.getId())}));

    }

    public void update(WordData data) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WORD, data.getWord());
        cv.put(COLUMN_MEANING, data.getMeaning());
        cv.put(COLUMN_IMAGE, data.getImage());
        cv.put(COLUMN_DICTIONARY, data.getDictionaryID());
        database.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(data.getId())});
    }

    @Override
    public synchronized void close() {
        super.close();
        database.close();
    }
}
