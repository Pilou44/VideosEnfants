package com.freak.videosenfants.app.settings

import com.freak.videosenfants.dagger.scope.PerActivity
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase
import com.freak.videosenfants.domain.useCase.GetLocalSourcesUseCase
import dagger.Module
import dagger.Provides


@Module
class SettingsModule {

    @Provides
    @PerActivity
    fun provideSettingsPresenter(getLocalRootsUseCase: GetLocalRootsUseCase, getLocalSourcesUseCase: GetLocalSourcesUseCase, router: SettingsContract.Router): SettingsContract.Presenter {
        return SettingsPresenter(getLocalRootsUseCase, getLocalSourcesUseCase, router)
    }

    @Provides
    @PerActivity
    fun provideSettingsRouter(): SettingsContract.Router {
        return SettingsRouter()
    }
}