package com.tobyrich.dev.hangarapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import com.tobyrich.dev.hangarapp.objects.Statistics;

/**
 * Created by Alex on 03.06.2015.
 * This class communicates with the database.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hangarapp_db",
        TABLE_STATISTICS = "statistics",
        KEY_ID = "id",
        KEY_NAME = "name",
        KEY_DATE = "date",
        KEY_VALUE = "value";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_STATISTICS + "(" + KEY_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_DATE + " TEXT, " +
            KEY_VALUE + " REAL)";

        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_STATISTICS;
        db.execSQL(sql);

        onCreate(db);
    }


    public void addStatistics(Statistics stats) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, stats.getStatName());
        values.put(KEY_DATE, stats.getDate());
        values.put(KEY_VALUE, stats.getValue());

        db.insert(TABLE_STATISTICS, null, values);
        db.close();
    }


    public Statistics getStatistics(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            TABLE_STATISTICS,
            new String[] { KEY_ID, KEY_NAME, KEY_DATE , KEY_VALUE },
            KEY_ID + "=?",
            new String[] { String.valueOf(id) },
            null,
            null,
            null,
            null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Statistics stats = new Statistics(
            Integer.parseInt(cursor.getString(0)),
            cursor.getString(1),
            cursor.getString(2),
            Double.parseDouble(cursor.getString(3))
        );

        cursor.close();
        db.close();

        return stats;
    }


    public void deleteStatistics(Statistics stats) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_STATISTICS, KEY_ID + "=?", new String[] { String.valueOf(stats.getId()) });
        db.close();
    }


    public int getStatsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STATISTICS, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }


    public int updateStatistics(Statistics stats) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, stats.getStatName());
        values.put(KEY_DATE, stats.getDate());
        values.put(KEY_VALUE, stats.getValue());


        return db.update(TABLE_STATISTICS, values, KEY_ID + "=?", new String[] { String.valueOf(stats.getId()) });
    }
}
