package com.freak.videosenfants.app.settings;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {
    private SettingsContract.View mView;

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (SettingsContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }
}
