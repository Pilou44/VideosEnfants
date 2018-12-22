package com.freak.videosenfants.app.home;

import android.util.Log;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import icepick.State;
import io.reactivex.observers.ResourceObserver;

public class HomePresenter extends BasePresenter implements HomeContract.Presenter {
    private static final String TAG = HomePresenter.class.getSimpleName();

    private final GetLocalRootsUseCase mGetLocalRootsUseCase;
    private final HomeContract.Router mRouter;

    private HomeContract.View mView;
    private boolean mLocalAvailable;

    public HomePresenter(@NotNull GetLocalRootsUseCase localRootsUseCase, @NotNull HomeContract.Router router) {
        mGetLocalRootsUseCase = localRootsUseCase;
        mRouter = router;
        mLocalAvailable = false;
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (HomeContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }

    @Override
    public void retrieveAvailableSources() {
        mGetLocalRootsUseCase.execute(new GetLocalRootsSubscriber());
    }

    private class GetLocalRootsSubscriber extends ResourceObserver<List<VideoElement>> {
        @Override
        public void onNext(List<VideoElement> videoElements) {
            mLocalAvailable = !videoElements.isEmpty();
            if (mView != null) {
                mView.notifyLocalSourcesAvailable(mLocalAvailable);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error getting local sources", new Exception(e));
        }

        @Override
        public void onComplete() {
            // Nothing to do
        }
    }
}
