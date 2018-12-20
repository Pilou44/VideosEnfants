package com.freak.videosenfants.app.browse.local

import com.freak.videosenfants.dagger.scope.PerActivity
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase
import com.freak.videosenfants.domain.useCase.GetThumbnailUseCase
import dagger.Module
import dagger.Provides

@Module
class BrowseLocalModule {

    @Provides
    @PerActivity
    fun provideBrowseLocalPresenter(getLocalRootsUseCase: GetLocalRootsUseCase,
                                    getThumbnailUseCase: GetThumbnailUseCase,
                                    router: BrowseLocalContract.Router): BrowseLocalContract.Presenter {
        return BrowseLocalPresenter(getLocalRootsUseCase, getThumbnailUseCase, router)
    }

    @Provides
    @PerActivity
    fun provideBrowseLocalRouter(): BrowseLocalContract.Router {
        return BrowseLocalRouter()
    }
}