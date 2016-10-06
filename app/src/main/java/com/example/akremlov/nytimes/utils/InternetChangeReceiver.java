package com.example.akremlov.nytimes.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


//this class is not yet implemented, it is merely a sketch
public class InternetChangeReceiver extends BroadcastReceiver {
    public InternetChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info;
        if ((info = connectivity.getActiveNetworkInfo()) != null) {
            if (info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING) {
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, " Not Connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, " Not Connected", Toast.LENGTH_SHORT).show();
        }
    }


}
