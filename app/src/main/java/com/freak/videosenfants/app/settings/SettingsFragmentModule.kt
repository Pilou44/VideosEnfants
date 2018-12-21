package com.freak.videosenfants.app.settings

import com.freak.videosenfants.app.settings.local.LocalPreferenceFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentModule {
    @ContributesAndroidInjector
    internal abstract fun bindLocalPreferenceFragment(): LocalPreferenceFragment
}