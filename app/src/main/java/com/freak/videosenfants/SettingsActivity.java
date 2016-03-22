package com.freak.videosenfants;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

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
    public static class LocalPreferenceFragment extends PreferenceFragment {
        private static final String TAG = LocalPreferenceFragment.class.getSimpleName();
        private static final boolean DEBUG = true;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_local);
        }

        @Override
        public void onPause() {
            super.onPause();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

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
