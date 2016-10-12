package com.example.akremlov.nytimes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.akremlov.nytimes.application.NewApplication;

public class NYSharedPreferences {

    private SharedPreferences mGeneralPreferences;

    private NYSharedPreferences() {
        this.mGeneralPreferences = NewApplication.getNewApplication().getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    private static NYSharedPreferences sInstance;

    public static NYSharedPreferences getsInstance() {
        if (sInstance == null) {
            sInstance = new NYSharedPreferences();
        }
        return sInstance;
    }


    private SharedPreferences getSharedPreferences() {
        return mGeneralPreferences;
    }

    public void setUserLoggedIn(boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.USER_LOGGED_IN, newValue);
        editor.apply();
    }

    public boolean getUserLoggedIn() {
        return getSharedPreferences().getBoolean(Constants.USER_LOGGED_IN, false);
    }

    public String getUsername() {
        return getSharedPreferences().getString(Constants.USERNAME, "");
    }

    public void setUserName(String name) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constants.USERNAME, name);
        editor.apply();
    }

    public void setCategoryPreference(String category, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(category, value);
        editor.apply();
    }

    public boolean getCategoryPreference(String category) {
        return getSharedPreferences().getBoolean(category, true);
    }

    public void clearPreferences() {
        getSharedPreferences().edit().clear().apply();
    }

}
