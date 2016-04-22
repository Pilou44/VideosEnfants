package com.freak.videosenfants.elements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.freak.videosenfants.R;

public class ApplicationSingleton {

    private static ApplicationSingleton mInstance;
    private final Context mContext;
    private boolean mParentMode;

    private ApplicationSingleton(Context context) {
        mParentMode = false;
        mContext = context;
    }

    public static synchronized ApplicationSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationSingleton(context);
        }
        return mInstance;
    }

    public boolean isParentMode() {
        return mParentMode;
    }

    public void setParentMode(boolean mParentMode) {
        this.mParentMode = mParentMode;
    }

    public String formatByteSize(long freeSpace) {
        if (freeSpace < 1024) {
            return freeSpace + " " + mContext.getString(R.string.bytes);
        }
        else {
            freeSpace = freeSpace / 1024;
            if (freeSpace < 1024) {
                return freeSpace + " " + mContext.getString(R.string.kilo_bytes);
            }
            else {
                freeSpace = freeSpace / 1024;
                if (freeSpace < 1024) {
                    return freeSpace + " " + mContext.getString(R.string.mega_bytes);
                }
                else {
                    freeSpace = freeSpace / 1024;
                    return freeSpace + " " + mContext.getString(R.string.giga_bytes);

                }
            }
        }
    }

    public boolean isWiFiConnected(){
        boolean wiFiConnected = false;
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connManager.getAllNetworks();
        for (Network network : networks) {
            NetworkInfo info = connManager.getNetworkInfo(network);
            if (info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
                wiFiConnected = true;
            }
        }
        return wiFiConnected;
    }
}
