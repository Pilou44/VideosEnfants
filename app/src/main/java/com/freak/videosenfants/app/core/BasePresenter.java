package com.freak.videosenfants.app.core;

import android.os.Bundle;

import icepick.Icepick;

public abstract class BasePresenter implements BaseContract.Presenter {

    @Override
    public void onRestore(Bundle bundle) {
        Icepick.restoreInstanceState(this, bundle);
    }

    @Override
    public void onSave(Bundle bundle) {
        Icepick.saveInstanceState(this, bundle);
    }
}
