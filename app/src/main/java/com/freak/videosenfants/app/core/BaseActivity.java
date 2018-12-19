package com.freak.videosenfants.app.core;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {

    public Context getContext() {
        return this;
    }
}
