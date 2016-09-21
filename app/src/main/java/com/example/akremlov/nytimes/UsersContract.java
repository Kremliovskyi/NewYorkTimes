package com.example.akremlov.nytimes;

import android.net.Uri;
import android.util.Base64;

/**
 * Created by akremlov on 20.09.16.
 */
public class UsersContract {
    public static final String AUTHORITY = "com.example.akremlov.nytimes.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://com.example.akremlov.nytimes.provider");
    public static final Uri TABLE_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(UserDb.TABLE_NAME).build();
    public static final String USERS_TYPE = "vnd.android.cursor.dir/vnd.com.example.akremlov.nytimes."+UserDb.TABLE_NAME;
    public static final String USER_TYPE = "vnd.android.cursor.item/vnd.com.example.akremlov.nytimes."+UserDb.TABLE_NAME;
    public static Uri buildUri(String string){
        return BASE_CONTENT_URI.buildUpon().appendEncodedPath(string).build();
    }
    public static String getUserId (Uri uri) {
        return uri.getPathSegments().get(1);
    }

}
