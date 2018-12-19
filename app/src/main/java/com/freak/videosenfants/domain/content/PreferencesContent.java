package com.freak.videosenfants.domain.content;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class PreferencesContent {

    private SharedPreferences mPreferences;

    @Inject
    public PreferencesContent(SharedPreferences sharedPreferences) {
        mPreferences = sharedPreferences;
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }
}
