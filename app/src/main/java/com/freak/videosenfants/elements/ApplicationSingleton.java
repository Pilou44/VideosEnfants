package com.freak.videosenfants.elements;

import android.content.Context;

public class ApplicationSingleton {

    private static ApplicationSingleton mInstance;
    private boolean mParentMode;

    public ApplicationSingleton(Context context) {
        mParentMode = false;
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
}
