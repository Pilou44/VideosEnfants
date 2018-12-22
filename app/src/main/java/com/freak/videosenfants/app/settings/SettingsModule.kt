package com.freak.videosenfants.app.settings

import com.freak.videosenfants.dagger.scope.PerActivity
import com.freak.videosenfants.domain.useCase.*
import dagger.Module
import dagger.Provides


@Module
class SettingsModule {

    @Provides
    @PerActivity
    fun provideSettingsPresenter(getLocalRootsUseCase: GetLocalRootsUseCase,
                                 getLocalSourcesUseCase: GetLocalSourcesUseCase,
                                 getLocalSubsUseCase: GetLocalSubsUseCase,
                                 addLocalRootUseCase: AddLocalRootUseCase,
                                 removeLocalRootUseCase: RemoveLocalRootUseCase,
                                 router: SettingsContract.Router): SettingsContract.Presenter {
        return SettingsPresenter(getLocalRootsUseCase,
                getLocalSourcesUseCase,
                getLocalSubsUseCase,
                addLocalRootUseCase,
                removeLocalRootUseCase,
                router)
    }

    @Provides
    @PerActivity
    fun provideSettingsRouter(): SettingsContract.Router {
        return SettingsRouter()
    }
}