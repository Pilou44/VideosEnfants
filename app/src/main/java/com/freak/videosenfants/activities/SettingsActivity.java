package com.freak.videosenfants.activities;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.preferences.BrowsePreference;
import com.freak.videosenfants.elements.preferences.MemoryPreference;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance(this).isParentMode())
            setTheme(R.style.AppTheme_ParentMode);
        else
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ApplicationSingleton.MY_PERMISSIONS_REQUEST_READ_STORAGE);

        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        root.addView(bar, 0);
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
                DlnaPreferenceFragment.class.getName().equals(fragmentName) ||
                MemoryPreferenceFragment.class.getName().equals(fragmentName);
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

    /**
     * This fragment shows the preferences for the dlna header.
     */
    public static class DlnaPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final boolean DEBUG = true;
        private static final String TAG = DlnaPreferenceFragment.class.getSimpleName();
        private Vector<BrowsePreference> mBrowsePrefs;
        private PreferenceScreen mScreen;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_dlna);

            mBrowsePrefs = new Vector<>();
            mScreen = getPreferenceScreen();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            int nbRoots = getResources().getInteger(R.integer.dlna_servers_number);
            for (int i = 0 ; i < nbRoots ; i++) {
                String key = getString(R.string.key_dlna_browse) + "_" + i;
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
                boolean visible = prefs.getBoolean(getString(R.string.key_dlna_browse) + "_" + i + getString(R.string.key_visible), false);
                boolean empty = prefs.getString(getString(R.string.key_dlna_browse) + "_" + i, "").length() == 0;

                if (DEBUG) {
                    Log.i(TAG, "dlna_browse_" + i + " visible: " + visible);
                    Log.i(TAG, "dlna_browse_" + i + " empty: " + empty);
                }

                if (!visible || empty) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(getString(R.string.key_dlna_browse) + "_" + i + getString(R.string.key_visible), false);
                    editor.remove(getString(R.string.key_dlna_browse) + "_" + i);
                    editor.apply();
                }
            }

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.startsWith(getString(R.string.key_dlna_browse)) && key.endsWith(getString(R.string.key_visible))){
                String indexString = key.substring(getString(R.string.key_dlna_browse).length() + 1 , key.lastIndexOf(getString(R.string.key_visible)));
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

    /**
     * This fragment shows the preferences for the memory usage header.
     */
    public static class MemoryPreferenceFragment extends PreferenceFragment {
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

}
