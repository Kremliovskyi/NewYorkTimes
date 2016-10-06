package com.example.akremlov.nytimes.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LogInSharedPreferences {

    private LogInSharedPreferences() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PreferenceFile, Context.MODE_PRIVATE);
    }

    public static void setBooleanValue(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(Constants.USER_LOGGED_IN, newValue);
        editor.apply();
    }

    public static boolean getBooleanValue(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.USER_LOGGED_IN, false);
    }

    public static String getUsername(Context context) {
        return getSharedPreferences(context).getString(Constants.USERNAME, "");
    }

    public static void setUserName(Context context, String name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Constants.USERNAME, name);
        editor.apply();
    }
}
