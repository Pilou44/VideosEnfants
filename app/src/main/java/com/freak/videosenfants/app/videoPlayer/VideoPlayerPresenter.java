package com.freak.videosenfants.app.videoPlayer;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;

public class VideoPlayerPresenter extends BasePresenter implements VideoPlayerContract.Presenter {

    private VideoPlayerContract.View mView;

    public VideoPlayerPresenter() {
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (VideoPlayerContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }
}
