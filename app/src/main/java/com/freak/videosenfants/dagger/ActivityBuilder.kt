package com.freak.videosenfants.dagger

import com.freak.videosenfants.app.browse.local.BrowseLocalActivity
import com.freak.videosenfants.app.browse.local.BrowseLocalModule
import com.freak.videosenfants.app.home.HomeActivity
import com.freak.videosenfants.app.home.HomeModule
import com.freak.videosenfants.app.settings.SettingsActivity
import com.freak.videosenfants.app.settings.SettingsFragmentModule
import com.freak.videosenfants.app.settings.SettingsModule
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

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(HomeModule::class))
    internal abstract fun bindHomeActivity(): HomeActivity

    @PerActivity
    @ContributesAndroidInjector(modules = arrayOf(SettingsModule::class, SettingsFragmentModule::class))
    internal abstract fun bindSettingsActivity(): SettingsActivity
}