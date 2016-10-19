package com.example.akremlov.nytimes.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.akremlov.nytimes.application.NewApplication;


public class InternetChangeReceiver extends BroadcastReceiver {

    private OnInternetListener mListener;

    public void setListener(OnInternetListener listener) {
        this.mListener = listener;
    }

    public InternetChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isInitialStickyBroadcast()) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info;
            if ((info = connectivity.getActiveNetworkInfo()) != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING) {
                    if (mListener != null) {
                        mListener.initiateLoading();
                    }
                }
            }
        }
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) NewApplication.getNewApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public interface OnInternetListener {
        void initiateLoading();
    }

}
