package com.freak.videosenfants.elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.freak.videosenfants.R;

public class ApplicationSingleton {

    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    private static ApplicationSingleton mInstance = new ApplicationSingleton();
    private boolean mParentMode;

    private ApplicationSingleton() {
        mParentMode = false;
    }

    public static synchronized ApplicationSingleton getInstance() {
        /*if (mInstance == null) {
            mInstance = new ApplicationSingleton(context);
        }*/
        return mInstance;
    }

    public boolean isParentMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return (prefs.getBoolean(context.getApplicationContext().getString(R.string.key_permanent_parent_mode), context.getApplicationContext().getResources().getBoolean(R.bool.default_permanent_parent_mode)) || mParentMode);
    }

    public void setParentMode(boolean mParentMode) {
        this.mParentMode = mParentMode;
    }

    public String formatByteSize(Context context, long freeSpace) {
        if (freeSpace < 1024) {
            return freeSpace + " " + context.getApplicationContext().getString(R.string.bytes);
        }
        else {
            freeSpace = freeSpace / 1024;
            if (freeSpace < 1024) {
                return freeSpace + " " + context.getApplicationContext().getString(R.string.kilo_bytes);
            }
            else {
                freeSpace = freeSpace / 1024;
                if (freeSpace < 1024) {
                    return freeSpace + " " + context.getApplicationContext().getString(R.string.mega_bytes);
                }
                else {
                    freeSpace = freeSpace / 1024;
                    return freeSpace + " " + context.getApplicationContext().getString(R.string.giga_bytes);

                }
            }
        }
    }

    public boolean isWiFiConnected(Context context){
        boolean wiFiConnected = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connManager.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo info = connManager.getNetworkInfo(network);
                if (info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
                    wiFiConnected = true;
                }
            }
        }
        else {
            //noinspection deprecation
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                wiFiConnected = true;
            }
        }
        return wiFiConnected;
    }
}
