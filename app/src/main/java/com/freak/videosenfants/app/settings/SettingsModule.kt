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
                                 geUpnpRootsUseCase: GetUpnpRootsUseCase,
                                 getLocalSourcesUseCase: GetLocalSourcesUseCase,
                                 getLocalSubsUseCase: GetLocalSubsUseCase,
                                 addLocalRootUseCase: AddLocalRootUseCase,
                                 removeLocalRootUseCase: RemoveLocalRootUseCase,
                                 removeUpnpRootUseCase: RemoveUpnpRootUseCase,
                                 listUpnpServersUseCase: ListUpnpServersUseCase,
                                 getUpnpSubsUseCase: GetUpnpSubsUseCase,
                                 addUpnpRootUseCase: AddUpnpRootUseCase,
                                 router: SettingsContract.Router): SettingsContract.Presenter {
        return SettingsPresenter(getLocalRootsUseCase,
                geUpnpRootsUseCase,
                getLocalSourcesUseCase,
                getLocalSubsUseCase,
                addLocalRootUseCase,
                removeLocalRootUseCase,
                removeUpnpRootUseCase,
                listUpnpServersUseCase,
                getUpnpSubsUseCase,
                addUpnpRootUseCase,
                router)
    }

    @Provides
    @PerActivity
    fun provideSettingsRouter(): SettingsContract.Router {
        return SettingsRouter()
    }
}