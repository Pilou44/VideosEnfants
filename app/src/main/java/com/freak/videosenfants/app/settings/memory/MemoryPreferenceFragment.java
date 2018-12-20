package com.freak.videosenfants.app.settings.memory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.preferences.MemoryPreference;

import java.io.File;

public class MemoryPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_memory);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        File downloaded = new File(sharedPref.getString(getActivity().getString(R.string.key_local_pictures), getActivity().getString(R.string.default_local_pictures)));
        MemoryPreference downloadedPref = (MemoryPreference) findPreference(getString(R.string.key_downloaded));
        downloadedPref.setDirectory(downloaded);

        File cached = getActivity().getExternalCacheDir();
        MemoryPreference cachedPref = (MemoryPreference) findPreference(getString(R.string.key_cached));
        cachedPref.setDirectory(cached);
    }
}
