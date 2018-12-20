package com.freak.videosenfants.app.settings.local;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.settings.SettingsActivity;
import com.freak.videosenfants.elements.preferences.BrowsePreference;

import java.util.Vector;

public class LocalPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LocalPreferenceFragment.class.getSimpleName();
    private static final boolean DEBUG = true;
    private Vector<BrowsePreference> mBrowsePrefs;
    private PreferenceScreen mScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_local);

        mBrowsePrefs = new Vector<>();
        mScreen = getPreferenceScreen();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        int nbRoots = getResources().getInteger(R.integer.local_roots_number);
        for (int i = 0 ; i < nbRoots ; i++) {
            String key = getString(R.string.key_local_browse) + "_" + i;
            mBrowsePrefs.add((BrowsePreference) findPreference(key));
            boolean visible = prefs.getBoolean(key + getString(R.string.key_visible), false);
            if (!visible) {
                mScreen.removePreference(mBrowsePrefs.get(i));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        int nbRoots = getResources().getInteger(R.integer.local_roots_number);

        for (int i = 0 ; i < nbRoots ; i++) {
            boolean visible = prefs.getBoolean(getString(R.string.key_local_browse) + "_" + i + getString(R.string.key_visible), false);
            boolean empty = prefs.getString(getString(R.string.key_local_browse) + "_" + i, "").length() == 0;

            if (DEBUG) {
                Log.i(TAG, "local_browse_" + i + " visible: " + visible);
                Log.i(TAG, "local_browse_" + i + " empty: " + empty);
            }

            if (!visible || empty) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.key_local_browse) + "_" + i + getString(R.string.key_visible), false);
                editor.remove(getString(R.string.key_local_browse) + "_" + i);
                editor.apply();
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith(getString(R.string.key_local_browse)) && key.endsWith(getString(R.string.key_visible))){
            String indexString = key.substring(getString(R.string.key_local_browse).length() + 1 , key.lastIndexOf(getString(R.string.key_visible)));
            int index = Integer.parseInt(indexString);
            if (sharedPreferences.getBoolean(key, false)) {
                mScreen.addPreference(mBrowsePrefs.get(index));
            }
            else {
                mScreen.removePreference(mBrowsePrefs.get(index));
            }
        }
    }
}
