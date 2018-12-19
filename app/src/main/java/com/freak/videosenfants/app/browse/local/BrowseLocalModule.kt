package com.freak.videosenfants.app.browse.local

import com.freak.videosenfants.dagger.scope.PerActivity
import dagger.Module
import dagger.Provides

@Module
class BrowseLocalModule {

    @Provides
    @PerActivity
    fun provideBrowseLocalPresenter(router: BrowseLocalContract.Router): BrowseLocalContract.Presenter {
        return BrowseLocalPresenter(router)
    }

    @Provides
    @PerActivity
    fun provideBrowseLocalRouter(): BrowseLocalContract.Router {
        return BrowseLocalRouter()
    }
}