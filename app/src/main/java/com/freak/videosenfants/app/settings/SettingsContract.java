package com.freak.videosenfants.app.settings;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.elements.preferences.FileElement;

import java.util.List;

public interface SettingsContract {

    interface View extends BaseContract.View {

        void refreshLocalRoots();

        void refreshLocalSources();
    }

    interface Presenter extends BaseContract.Presenter {

        List<VideoElement> getLocalRoots();

        void removeLocalRoot(VideoElement element);

        void showMainSettings(int fragment);

        void showLocalSettings(int id);

        void showDlnaSettings(int id);

        void showGeneralSettings(int id);

        void showMemorySettings(int id);

        List<FileElement> getLocalFiles();

        void retrieveLocalSources();
    }

    interface Router extends BaseContract.Router {

        void showMainSettings(View view, int fragmentId);

        void showLocalSettings(View view, int fragmentId);

        void showDlnaSettings(View view, int fragmentId);

        void showGeneralSettings(View view, int fragmentId);

        void showMemorySettings(View view, int fragmentId);
    }
}
