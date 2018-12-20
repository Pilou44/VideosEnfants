package com.freak.videosenfants.app.settings.general;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.freak.videosenfants.R;

public class GeneralPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
    }
}
