package com.freak.videosenfants.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = StartActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    private ImageButton mVoiture;
    private ImageButton mMaison;
    private ImageButton mOptions;
    private MenuItem mParentMode;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mMaison = (ImageButton) findViewById(R.id.maison);
        assert mMaison != null;
        mMaison.setOnClickListener(this);

        mVoiture = (ImageButton) findViewById(R.id.voiture);
        assert mVoiture != null;
        mVoiture.setOnClickListener(this);

        mOptions = (ImageButton) findViewById(R.id.options);
        assert mOptions != null;
        mOptions.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateParentMode(ApplicationSingleton.getInstance(this).isParentMode());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (sharedPreferences.getBoolean(getString(R.string.key_local_switch), getResources().getBoolean(R.bool.default_switch_local))) {
            mVoiture.setVisibility(View.VISIBLE);
        }
        else {
            mVoiture.setVisibility(View.GONE);
        }

        if (sharedPreferences.getBoolean(getString(R.string.key_dlna_switch), getResources().getBoolean(R.bool.default_switch_dlna))) {
            mMaison.setVisibility(View.VISIBLE);
        }
        else {
            mMaison.setVisibility(View.GONE);
        }

        if (mMaison.getVisibility() == View.GONE && mVoiture.getVisibility() == View.GONE) {
            mOptions.setVisibility(View.VISIBLE);
        }
        else {
            mOptions.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        mParentMode = menu.findItem(R.id.parent_mode);
        if (DEBUG)
            Log.i(TAG, "Parent mode: " + ApplicationSingleton.getInstance(this).isParentMode());
        mParentMode.setChecked(ApplicationSingleton.getInstance(this).isParentMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.parent_mode:
                mParentMode.setChecked(!mParentMode.isChecked());
                ApplicationSingleton.getInstance(this).setParentMode(mParentMode.isChecked());
                if (DEBUG)
                    Log.i(TAG, "Parent mode: " + ApplicationSingleton.getInstance(this).isParentMode());
                updateParentMode(ApplicationSingleton.getInstance(this).isParentMode());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateParentMode(boolean parentMode) {
        if (parentMode)
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorParent));
        else
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.voiture){
            intent = new Intent(this, BrowseSDActivity.class);
        }
        else if (view.getId() == R.id.maison){
            intent = new Intent(this, BrowseDlnaActivity.class);
        }
        else if (view.getId() == R.id.options) {
            intent = new Intent(this, SettingsActivity.class);
        }
        startActivity(intent);
    }
}
