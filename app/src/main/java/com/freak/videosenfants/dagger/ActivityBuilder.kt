package com.freak.videosenfants.dagger

import com.freak.videosenfants.app.videoPlayer.VideoPlayerActivity
import com.freak.videosenfants.dagger.scope.PerActivity
import com.viadeo.phoenix.android.modules.videoPlayer.VideoPlayerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(VideoPlayerModule::class))
    internal abstract fun bindVideoPlayerActivity(): VideoPlayerActivity
}