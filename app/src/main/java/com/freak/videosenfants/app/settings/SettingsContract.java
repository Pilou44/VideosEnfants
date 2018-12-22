package com.freak.videosenfants.app.settings;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.domain.bean.BaseElement;
import com.freak.videosenfants.domain.bean.BrowsableElement;
import com.freak.videosenfants.domain.bean.FileElement;

import java.util.List;

public interface SettingsContract {

    interface View extends BaseContract.View {

        void refreshRoots();

        void refreshSources();

        void notifySubsRetrieved(int position, int size);
    }

    interface Presenter extends BaseContract.Presenter {

        int TYPE_LOCAL = 1;
        int TYPE_UPNP = 2;

        List<BaseElement> getRoots(int type);

        void removeRoot(int type, BaseElement element);

        void showMainSettings(int fragment);

        void showLocalSettings(int id);

        void showDlnaSettings(int id);

        void showGeneralSettings(int id);

        void showMemorySettings(int id);

        List<BrowsableElement> getFiles(int type);

        void retrieveSources(int type);

        void expand(BrowsableElement element, int type);

        void addRoot(BrowsableElement element, int type);

        void retrieveRoots(int type);
    }

    interface Router extends BaseContract.Router {

        void showMainSettings(View view, int fragmentId);

        void showLocalSettings(View view, int fragmentId);

        void showDlnaSettings(View view, int fragmentId);

        void showGeneralSettings(View view, int fragmentId);

        void showMemorySettings(View view, int fragmentId);
    }
}
