package com.example.akremlov.nytimes.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.example.akremlov.nytimes.database.UserDb;

/**
 * Created by akremlov on 20.09.16.
 */
public class UsersProvider extends ContentProvider {

    private UserDb mDb;
    private UriMatcher mMatcher = buildMatcher();
    private static final int USER = 100;
    private static final int USERS = 101;

    private UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(UsersContract.AUTHORITY, UserDb.TABLE_NAME, USERS);
        matcher.addURI(UsersContract.AUTHORITY, UserDb.TABLE_NAME + "/*", USER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDb = new UserDb(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDb.getReadableDatabase();
        int code = mMatcher.match(uri);
        switch (code) {
            case USER:
                return database.query(UserDb.TABLE_NAME, new String[]{UserDb.DBColumns.USERNAME, UserDb.DBColumns.EMAIL,
                        UserDb.DBColumns.PASSWORD, UserDb.DBColumns.PATH_TO_IMAGE}, BaseColumns._ID + " = " +
                        UsersContract.getUserId(uri), selectionArgs, null, null, sortOrder);
            case USERS:
                return database.query(UserDb.TABLE_NAME, projection, null, null, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Incorrect URI " + uri);

        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int code = mMatcher.match(uri);
        switch (code) {
            case USER:
                return UsersContract.USER_TYPE;
            case USERS:
                return UsersContract.USERS_TYPE;
            default:
                throw new IllegalArgumentException("Incorrect URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDb.getWritableDatabase();
        int code = mMatcher.match(uri);
        switch (code) {
            case USERS:
                long rowId = database.insertOrThrow(UserDb.TABLE_NAME, null, values);
                return UsersContract.buildUri(String.valueOf(rowId));
            default:
                throw new IllegalArgumentException("Incorrect URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDb.getWritableDatabase();
        int code = mMatcher.match(uri);
        switch (code) {
            case USER:
                String id = UsersContract.getUserId(uri);
                return database.delete(UserDb.TABLE_NAME, BaseColumns._ID + " = " + id, selectionArgs);
            default:
                throw new IllegalArgumentException("Incorrect URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDb.getWritableDatabase();
        int code = mMatcher.match(uri);
        switch (code) {
            case USERS:
                return database.update(UserDb.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Incorrect URI " + uri);
        }
    }
}
