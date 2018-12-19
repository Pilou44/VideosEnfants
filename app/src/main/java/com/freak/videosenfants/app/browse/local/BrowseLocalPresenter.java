package com.freak.videosenfants.app.browse.local;

import android.net.Uri;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;

public class BrowseLocalPresenter extends BasePresenter implements BrowseLocalContract.Presenter {
    private final BrowseLocalContract.Router mRouter;
    private BrowseLocalContract.View mView;

    public BrowseLocalPresenter(BrowseLocalContract.Router router) {
        mRouter = router;
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (BrowseLocalContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }

    @Override
    public void playVideo(Uri videoUri) {
        mRouter.playVideoWithAndroid(mView, videoUri);
    }
}
