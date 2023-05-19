package ru.vladsa.wordteacher.dictionaries.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import ru.vladsa.wordteacher.dictionaries.DictionaryData;

public class DictionaryDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dictionary.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "dictiories";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_WORD_COUNT = "count";

    private static final long NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_TITLE = 1;
    private static final int NUM_COLUMN_VALUE = 2;
    private static final int NUM_WORD_COUNT = 3;

    SQLiteDatabase database;

    public DictionaryDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        database = getWritableDatabase();
    }


    public void add(DictionaryData data) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, data.getName());
        cv.put(COLUMN_VALUE, data.getValue());
        cv.put(COLUMN_WORD_COUNT, data.getWordCount());
        database.insert(TABLE_NAME, null, cv);
    }

    public ArrayList<DictionaryData> getAll() {
        ArrayList<DictionaryData> result = new ArrayList<>();
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
            result.add(new DictionaryData(
                    cursor.getString(NUM_COLUMN_TITLE),
                    cursor.getLong(NUM_WORD_COUNT),
                    Boolean.parseBoolean(cursor.getString(NUM_COLUMN_VALUE))
            ));
        } while (cursor.moveToNext());
        cursor.close();
        return result;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_VALUE + " TEXT, " +
                COLUMN_WORD_COUNT + " INTEGER); ";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void deleteDictionary(DictionaryData data) {
        Log.d("DeleteSQLData", " " + data.getName() + " " + database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(data.getId())}));

    }

    public void update(DictionaryData data) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, data.getName());
        cv.put(COLUMN_VALUE, data.getValue());
        database.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(data.getId())});
    }

    @Override
    public synchronized void close() {
        super.close();
        database.close();
    }
}
