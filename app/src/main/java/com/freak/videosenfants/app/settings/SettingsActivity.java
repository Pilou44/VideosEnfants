package com.freak.videosenfants.app.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.settings.dlna.DlnaPreferenceFragment;
import com.freak.videosenfants.app.settings.general.GeneralPreferenceFragment;
import com.freak.videosenfants.app.settings.local.LocalPreferenceFragment;
import com.freak.videosenfants.app.settings.memory.MemoryPreferenceFragment;
import com.freak.videosenfants.elements.ApplicationSingleton;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance().isParentMode(this))
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

}
