package com.example.trackme;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Optional;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "LocationsDB";

    public static final String TABLE_LOCATIONS = "location";
    public static final String KEY_ID = "id";
    public static final String KEY_LAT = "Latitude";
    public static final String KEY_LON = "Longitude";
    public static final String KEY_NOTE = "Note";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_LOCATIONS + "(" +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NOTE + " INTEGER, "
                + KEY_LAT +" DOUBLE, "
                + KEY_LON +" DOUBLE )";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LOCATIONS);
        onCreate(db);
    }
}
