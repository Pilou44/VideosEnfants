package com.freak.videosenfants.app.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.core.BaseActivity;
import com.freak.videosenfants.app.settings.local.BrowseLocalDialogFragment;
import com.freak.videosenfants.app.settings.local.LocalPreferenceFragment;
import com.freak.videosenfants.elements.ApplicationSingleton;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SettingsActivity extends BaseActivity implements SettingsContract.View, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> mSupportFragmentInjector;


    @Inject
    SettingsContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance().isParentMode(this))
            setTheme(R.style.AppTheme_ParentMode);
        else
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        AndroidInjection.inject(this);
        mPresenter.subscribe(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ApplicationSingleton.MY_PERMISSIONS_REQUEST_READ_STORAGE);

        }

        mPresenter.showMainSettings(R.id.fragment);
    }

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        AppBarLayout bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        root.addView(bar, 0);
    }
    
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
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mSupportFragmentInjector;
    }

    @Override
    public void refreshRoots() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof LocalPreferenceFragment) {
            ((LocalPreferenceFragment) fragment).refreshRoots();
        }
    }

    @Override
    public void refreshSources() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(0);
        if (fragment instanceof BrowseLocalDialogFragment) {
            ((BrowseLocalDialogFragment) fragment).refreshLocalSources();
        }
    }

    @Override
    public void notifySubsRetrieved(int position, int size) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(0);
        if (fragment instanceof BrowseLocalDialogFragment) {
            ((BrowseLocalDialogFragment) fragment).notifyLocalSubsRetrieved(position, size);
        }
    }
}
