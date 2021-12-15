package com.example.mobile2021_02_grupo03.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicAppDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;

    public static final String DATABASE_NAME = "musicApp.db";

    private static final String SQL_CREATE_TABLE_SONGS = "CREATE TABLE " + MusicAppDBContract.songsTable.TABLE_NAME + " (" +
            MusicAppDBContract.songsTable._ID + " INTEGER PRIMARY KEY," +
            MusicAppDBContract.songsTable.COLUMN_NAME_NAME + " TEXT," +
            MusicAppDBContract.songsTable.COLUMN_NAME_PATH + " TEXT)";

    private static final String SQL_CREATE_TABLE_RECENT_SONGS = "CREATE TABLE " + MusicAppDBContract.recentSongsTable.TABLE_NAME + " (" +
            MusicAppDBContract.recentSongsTable._ID + " INTEGER PRIMARY KEY," +
            MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME + " TEXT," +
            MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH + " TEXT," +
            MusicAppDBContract.recentSongsTable.COLUMN_NAME_PLAYLIST + " TEXT)";

    private static final String SQL_CREATE_TABLE_FAVORITE_SONGS = "CREATE TABLE IF NOT EXISTS " + MusicAppDBContract.favoriteSongsTable.TABLE_NAME + " (" +
            MusicAppDBContract.recentSongsTable._ID + " INTEGER PRIMARY KEY," +
            MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME + " TEXT," +
            MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH + " TEXT)";

    private static final String SQL_DROP_TABLE_SONGS = "DROP TABLE IF EXISTS " + MusicAppDBContract.songsTable.TABLE_NAME;

    private static final String SQL_DROP_TABLE_RECENT_SONGS = "DROP TABLE IF EXISTS " + MusicAppDBContract.recentSongsTable.TABLE_NAME;

    private static final String SQL_DROP_TABLE_FAVORITE_SONGS = "DROP TABLE IF EXISTS " + MusicAppDBContract.favoriteSongsTable.TABLE_NAME;

    public MusicAppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SONGS);
        db.execSQL(SQL_CREATE_TABLE_RECENT_SONGS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DROP_TABLE_SONGS);
        db.execSQL(SQL_DROP_TABLE_RECENT_SONGS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_TABLE_SONGS);
        db.execSQL(SQL_DROP_TABLE_RECENT_SONGS);
    }

    public void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SONGS);
        db.execSQL(SQL_CREATE_TABLE_RECENT_SONGS);
        db.execSQL(SQL_CREATE_TABLE_FAVORITE_SONGS);
    }

    public void insert(SQLiteDatabase db, String tableName, String name, String path) {
        ContentValues values = new ContentValues();
        values.put(MusicAppDBContract.songsTable.COLUMN_NAME_NAME, name);
        values.put(MusicAppDBContract.songsTable.COLUMN_NAME_PATH, path);

        db.insert(tableName, null, values);
    }

    public void delete(SQLiteDatabase db, String tableName, String name){
        db.execSQL("delete from " + tableName + " where name = '" + name + "'");
    }

    public void insert(SQLiteDatabase db, String tableName, String name, String path, String playlist) {
            db.execSQL("delete from " + tableName + " where name = '" + name + "'");

            ContentValues values = new ContentValues();
            values.put(MusicAppDBContract.recentSongsTable.COLUMN_NAME_NAME, name);
            values.put(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PATH, path);
            values.put(MusicAppDBContract.recentSongsTable.COLUMN_NAME_PLAYLIST, playlist);

            db.insert(tableName, null, values);
    }

    public Cursor select(SQLiteDatabase db, String tableName, String[] columns, String where, String[] whereArgs, String orderBy) {
        Cursor cursor = db.query(
                tableName,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                whereArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                orderBy               // The sort order
        );
        return cursor;
    }

    public Cursor select(SQLiteDatabase db, String tableName, String[] columns, String where, String[] whereArgs) {
        Cursor cursor = db.query(
                tableName,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                where,              // The columns for the WHERE clause
                whereArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        return cursor;
    }

    public Cursor select(SQLiteDatabase db, String tableName, String[] columns, String orderBy) {
        Cursor cursor = db.query(
                tableName,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                orderBy               // The sort order
        );
        return cursor;
    }

    public Cursor select(SQLiteDatabase db, String tableName, String[] columns) {
        Cursor cursor = db.query(
                tableName,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        return cursor;
    }
}
