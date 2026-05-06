package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "adverts";

    private static final String COL_ID = "id";
    private static final String COL_TYPE = "type";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LOCATION = "location";
    private static final String COL_CATEGORY = "category";
    private static final String COL_IMAGE_URI = "image_uri";
    private static final String COL_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TYPE + " TEXT, " +
                        COL_NAME + " TEXT, " +
                        COL_PHONE + " TEXT, " +
                        COL_DESCRIPTION + " TEXT, " +
                        COL_DATE + " TEXT, " +
                        COL_LOCATION + " TEXT, " +
                        COL_CATEGORY + " TEXT, " +
                        COL_IMAGE_URI + " TEXT, " +
                        COL_TIMESTAMP + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAdvert(Advert advert) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TYPE, advert.getType());
        values.put(COL_NAME, advert.getName());
        values.put(COL_PHONE, advert.getPhone());
        values.put(COL_DESCRIPTION, advert.getDescription());
        values.put(COL_DATE, advert.getDate());
        values.put(COL_LOCATION, advert.getLocation());
        values.put(COL_CATEGORY, advert.getCategory());
        values.put(COL_IMAGE_URI, advert.getImageUri());
        values.put(COL_TIMESTAMP, advert.getTimestamp());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }

    public ArrayList<Advert> getAllAdverts() {
        ArrayList<Advert> advertList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))
                );

                advertList.add(advert);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return advertList;
    }

    public ArrayList<Advert> getAdvertsByCategory(String category) {
        ArrayList<Advert> advertList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CATEGORY + " = ? ORDER BY " + COL_ID + " DESC",
                new String[]{category}
        );

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))
                );

                advertList.add(advert);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return advertList;
    }

    public Advert getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        Advert advert = null;

        if (cursor.moveToFirst()) {
            advert = new Advert(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))
            );
        }

        cursor.close();
        db.close();

        return advert;
    }

    public boolean deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result > 0;
    }
}