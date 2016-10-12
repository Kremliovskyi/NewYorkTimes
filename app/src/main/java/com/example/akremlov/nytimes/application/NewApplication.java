package com.example.akremlov.nytimes.application;

import android.app.Application;

/**
 * Created by akremlov on 11.10.16.
 */

public class NewApplication extends Application {

    private static NewApplication mThis;

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
    }

    public static NewApplication getNewApplication() {
        return mThis;
    }
}
