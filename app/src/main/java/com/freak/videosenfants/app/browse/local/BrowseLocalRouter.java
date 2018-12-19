package com.freak.videosenfants.app.browse.local;

import android.content.Intent;
import android.net.Uri;

import com.freak.videosenfants.app.core.BaseRouter;

public class BrowseLocalRouter extends BaseRouter implements BrowseLocalContract.Router {

    @Override
    public void playVideoWithAndroid(BrowseLocalContract.View view, Uri videoUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
        intent.setDataAndType(videoUri, "video/*");
        getActivity(view).startActivity(intent);
    }
}
