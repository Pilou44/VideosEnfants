package com.freak.videosenfants.app.browse.local;

import android.net.Uri;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.domain.bean.VideoElement;

import java.util.List;

public interface BrowseLocalContract {

    interface View extends BaseContract.View {

        void notifyElementsUpdated();

        void showElementThumbnail(VideoElement element, Uri uri);
    }

    interface Presenter extends BaseContract.Presenter {

        void playVideo(Uri videoUri);

        List<VideoElement> getCurrentItems();

        VideoElement getItem(int position);

        void browseLocal(VideoElement element);

        void goBack();

        void getImageUri(VideoElement element);

        void retrieveLocalRoots();
    }

    interface Router extends BaseContract.Router {

        void playVideoWithAndroid(View view, Uri videoUri);

        void goStartActivity(View view);
    }
}
