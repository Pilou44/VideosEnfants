package com.viadeo.phoenix.android.modules.videoPlayer

import com.freak.videosenfants.app.videoPlayer.VideoPlayerContract
import com.freak.videosenfants.app.videoPlayer.VideoPlayerPresenter
import com.freak.videosenfants.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class VideoPlayerModule {

    @Provides
    @PerActivity
    fun provideVideoPlayerPresenter(): VideoPlayerContract.Presenter {
        return VideoPlayerPresenter()
    }
}