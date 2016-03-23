package com.freak.videosenfants;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.freak.videosenfants.elements.preferences.AddButtonPreference;
import com.freak.videosenfants.elements.preferences.BrowseLocalPreference;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return
                GeneralPreferenceFragment.class.getName().equals(fragmentName) ||
                LocalPreferenceFragment.class.getName().equals(fragmentName) ||
                DlnaPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows the preferences for the general header.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    /**
     * This fragment shows the preferences for the local header.
     */
    public static class LocalPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String TAG = LocalPreferenceFragment.class.getSimpleName();
        private static final boolean DEBUG = true;
        private SwitchPreference mSwitchButton;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_local);
            mSwitchButton = (SwitchPreference) findPreference(getString(R.string.key_local_switch));
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
            setEnabled();
        }

        @Override
        public void onPause() {
            super.onPause();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            prefs.unregisterOnSharedPreferenceChangeListener(this);

            int nbRoots = getResources().getInteger(R.integer.local_roots_number);

            for (int i = 0 ; i < nbRoots ; i++) {
                boolean visible = prefs.getBoolean("local_browse_" + i + "_visible", false);
                boolean empty = prefs.getString("local_browse_" + i, "").length() == 0;

                if (DEBUG) {
                    Log.i(TAG, "local_browse_" + i + " visible: " + visible);
                    Log.i(TAG, "local_browse_" + i + " empty: " + empty);
                }

                if (!visible || empty) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("local_browse_" + i + "_visible", false);
                    editor.remove("local_browse_" + i);
                    editor.apply();
                }
            }

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.key_local_switch))) {
                setEnabled();
            }
            else if (key.startsWith(getString(R.string.key_local_browse)) && key.endsWith(getString(R.string.key_visible))){
                String prefKey = key.substring(0, key.lastIndexOf(getString(R.string.key_visible)));
                BrowseLocalPreference pref = (BrowseLocalPreference) findPreference(prefKey);
                pref.setVisible(sharedPreferences.getBoolean(key, false));
            }
        }

        private void setEnabled() {
            int nbRoots = getResources().getInteger(R.integer.local_roots_number);
            for (int i = 0 ; i < nbRoots ; i++) {
                String prefKey = getString(R.string.key_local_browse) + "_" + i;
                BrowseLocalPreference pref = (BrowseLocalPreference) findPreference(prefKey);
                pref.setEnabled(mSwitchButton.isChecked());
            }
            AddButtonPreference addPref = (AddButtonPreference) findPreference(getString(R.string.key_local_browse));
            addPref.setEnabled(mSwitchButton.isChecked());
        }
    }

    /**
     * This fragment shows the preferences for the dlna header.
     */
    public static class DlnaPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_dlna);
        }
    }
}
