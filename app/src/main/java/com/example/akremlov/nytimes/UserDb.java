package com.example.akremlov.nytimes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by akremlov on 20.09.16.
 */
public class UserDb extends SQLiteOpenHelper{
    public static final String DB_NAME = "users.db";
    public static final String TABLE_NAME = "users";
    public static final int DB_VERSION = 1;
    interface DBColumns {
        String ID = "_id";
        String USERNAME = "USERNAME";
        String EMAIL = "EMAIL";
        String PASSWORD = "PASSWORD";
        String PATH_TO_IMAGE = "PATH_TO_IMAGE";
    }
    public UserDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+ BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
        DBColumns.USERNAME+" TEXT NOT NULL, "+DBColumns.EMAIL+" TEXT NOT NULL, "+DBColumns.PASSWORD+
                " TEXT NOT NULL, "+DBColumns.PATH_TO_IMAGE+" TEXT NOT NULL DEFAULT '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
