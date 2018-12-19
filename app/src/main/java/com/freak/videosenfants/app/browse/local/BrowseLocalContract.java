package com.freak.videosenfants.app.browse.local;

import android.net.Uri;

import com.freak.videosenfants.app.core.BaseContract;

public interface BrowseLocalContract {

    interface View extends BaseContract.View {

    }

    interface Presenter extends BaseContract.Presenter {

        void playVideo(Uri videoUri);
    }

    interface Router extends BaseContract.Router {

        void playVideoWithAndroid(View view, Uri videoUri);
    }
}
