package com.main.carassistant.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChecker {

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo con = connectivityManager.getActiveNetworkInfo();
        if (con!=null) {
            return connectivityManager.getActiveNetworkInfo().isConnected();
        } else return false;
    }
}