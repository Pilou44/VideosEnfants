package com.freak.videosenfants.app.home;

import com.freak.videosenfants.app.core.BaseContract;

public interface HomeContract {

    interface View extends BaseContract.View {

        void notifyLocalSourcesAvailable(boolean localAvailable);
    }

    interface Presenter extends BaseContract.Presenter {

        void retrieveAvailableSources();
    }

    interface Router extends BaseContract.Router {

    }
}
