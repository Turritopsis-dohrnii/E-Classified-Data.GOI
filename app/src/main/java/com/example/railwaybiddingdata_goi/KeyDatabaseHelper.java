package com.example.railwaybiddingdata_goi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "keyDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "keys";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_KEY = "encryptionKey";

    public KeyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_KEY + " BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertKey(String id, byte[] key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_KEY, key);
        db.insert(TABLE_NAME, null, values);
    }

    public byte[] getKey(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_KEY}, COLUMN_ID + "=?",
                new String[]{id}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            byte[] key = cursor.getBlob(0);
            cursor.close();
            return key;
        }
        return null;
    }
}
