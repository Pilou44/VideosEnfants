package com.freak.videosenfants.elements;

import android.content.Context;

import com.freak.videosenfants.R;

public class ApplicationSingleton {

    private static ApplicationSingleton mInstance;
    private final Context mContext;
    private boolean mParentMode;

    public ApplicationSingleton(Context context) {
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
}
