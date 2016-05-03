package com.freak.videosenfants.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.freak.videosenfants.R;

public class HelpActivity extends AppCompatActivity {

    private int mId;
    private TextView mTitle;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) findViewById(R.id.title);
        mText = (TextView) findViewById(R.id.text);

        mId = getIntent().getIntExtra(getString(R.string.key_id), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (mId) {
            case R.id.configure_local:
                mTitle.setText(R.string.configure_local);
                mText.setText(R.string.configure_local_text);
                break;
            case R.id.configure_upnp:
                mTitle.setText(R.string.configure_upnp);
                mText.setText(R.string.configure_upnp_text);
                break;
            case R.id.parent_mode:
                mTitle.setText(R.string.go_parent_mode);
                mText.setText(R.string.go_parent_mode_text);
                break;
            case R.id.copy_to_local:
                mTitle.setText(R.string.copy_to_local);
                mText.setText(R.string.copy_to_local_text);
                break;
            case R.id.find_thumbnail:
                mTitle.setText(R.string.find_thumbnail);
                mText.setText(R.string.find_thumbnail_text);
                break;
            case R.id.delete_local:
                mTitle.setText(R.string.delete_local);
                mText.setText(R.string.delete_local_text);
                break;
            default:
                mTitle.setText("");
                mText.setText("");
                break;
        }
    }
}
