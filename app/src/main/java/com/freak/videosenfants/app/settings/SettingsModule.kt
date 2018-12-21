package com.freak.videosenfants.app.settings

import com.freak.videosenfants.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides


@Module
class SettingsModule {

    @Provides
    @PerActivity
    fun provideSettingsPresenter(): SettingsContract.Presenter {
        return SettingsPresenter()
    }
}