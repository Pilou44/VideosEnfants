package com.freak.videosenfants.app.home

import com.freak.videosenfants.dagger.scope.PerActivity
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    @PerActivity
    fun provideHomePresenter(getLocalRootsUseCase: GetLocalRootsUseCase,
                             router: HomeContract.Router): HomeContract.Presenter {
        return HomePresenter(getLocalRootsUseCase,
                router)
    }

    @Provides
    @PerActivity
    fun provideHomeRouter(): HomeContract.Router {
        return HomeRouter()
    }
}