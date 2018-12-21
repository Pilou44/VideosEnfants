package com.freak.videosenfants.app.settings;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalSourcesUseCase;
import com.freak.videosenfants.elements.preferences.FileElement;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.ResourceObserver;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {
    private final ArrayList<VideoElement> mLocalRoots;
    private final SettingsContract.Router mRouter;
    private SettingsContract.View mView;
    private List<FileElement> mLocalFiles;
    private GetLocalSourcesUseCase mGetLocalSourcesUseCase;

    public SettingsPresenter(GetLocalRootsUseCase getLocalRootsUseCase, GetLocalSourcesUseCase getLocalSourcesUseCase, SettingsContract.Router router) {
        mGetLocalSourcesUseCase = getLocalSourcesUseCase;
        mRouter = router;

        getLocalRootsUseCase.execute(new GetLocalRootsSubscriber());

        mLocalRoots = new ArrayList<>();
        mLocalFiles = new ArrayList<>();
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (SettingsContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }

    @Override
    public List<VideoElement> getLocalRoots() {
        return mLocalRoots;
    }

    @Override
    public void removeLocalRoot(VideoElement element) {
        // ToDo
    }

    @Override
    public void showMainSettings(int fragmentId) {
        mRouter.showMainSettings(mView, fragmentId);
    }

    @Override
    public void showLocalSettings(int fragmentId) {
        mRouter.showLocalSettings(mView, fragmentId);
    }

    @Override
    public void showDlnaSettings(int fragmentId) {
        mRouter.showDlnaSettings(mView, fragmentId);
    }

    @Override
    public void showGeneralSettings(int fragmentId) {
        mRouter.showGeneralSettings(mView, fragmentId);
    }

    @Override
    public void showMemorySettings(int fragmentId) {
        mRouter.showMemorySettings(mView, fragmentId);
    }

    @Override
    public List<FileElement> getLocalFiles() {
        return mLocalFiles;
    }

    @Override
    public void retrieveLocalSources() {
        mLocalFiles.clear();
        mGetLocalSourcesUseCase.execute(new GetLocalSourcesSubscriber());
    }

    private class GetLocalRootsSubscriber extends ResourceObserver<List<VideoElement>> {
        @Override
        public void onNext(List<VideoElement> videoElements) {
            mLocalRoots.addAll(videoElements);
            if (mView != null) {
                mView.refreshLocalRoots();
            }
        }

        @Override
        public void onError(Throwable e) {
            // ToDo
        }

        @Override
        public void onComplete() {
            // Nothing to do
        }
    }

    private class GetLocalSourcesSubscriber extends ResourceObserver<List<FileElement>> {
        @Override
        public void onNext(List<FileElement> fileElements) {
            mLocalFiles.addAll(fileElements);
            if (mView != null) {
                mView.refreshLocalSources();
            }
        }

        @Override
        public void onError(Throwable e) {
            // ToDo
        }

        @Override
        public void onComplete() {
            // Nothing to do
        }
    }
}
