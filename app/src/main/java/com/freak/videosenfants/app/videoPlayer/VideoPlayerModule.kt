package com.freak.videosenfants.app.videoPlayer

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