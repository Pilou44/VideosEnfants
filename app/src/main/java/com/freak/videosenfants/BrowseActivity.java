package com.freak.videosenfants;

import android.support.v7.app.AppCompatActivity;

public abstract class BrowseActivity extends AppCompatActivity {

    protected VideoElement mCurrent;
    protected String mRoot = "root";

    @Override
    public void onBackPressed() {
        if (mCurrent == null || mCurrent.getPath().equals(mRoot)) {
            super.onBackPressed();
        }
        else {
            parseAndUpdate(mCurrent.getParent());
        }
    }

    protected abstract void parseAndUpdate(VideoElement parent);

}
