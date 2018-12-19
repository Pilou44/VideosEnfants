package com.freak.videosenfants.dagger

import com.freak.videosenfants.app.browse.local.BrowseLocalActivity
import com.freak.videosenfants.app.browse.local.BrowseLocalModule
import com.freak.videosenfants.app.videoPlayer.VideoPlayerActivity
import com.freak.videosenfants.dagger.scope.PerActivity
import com.freak.videosenfants.app.videoPlayer.VideoPlayerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(VideoPlayerModule::class))
    internal abstract fun bindVideoPlayerActivity(): VideoPlayerActivity

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(BrowseLocalModule::class))
    internal abstract fun bindBrowseLocalActivity(): BrowseLocalActivity
}