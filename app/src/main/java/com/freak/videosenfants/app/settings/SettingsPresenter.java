package com.freak.videosenfants.app.settings;

import android.util.Log;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.useCase.AddLocalRootUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalSourcesUseCase;
import com.freak.videosenfants.domain.bean.FileElement;
import com.freak.videosenfants.domain.useCase.GetLocalSubsUseCase;
import com.freak.videosenfants.domain.useCase.RemoveLocalRootUseCase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.ResourceObserver;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {
    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final ArrayList<VideoElement> mLocalRoots;
    private final SettingsContract.Router mRouter;
    private final GetLocalSubsUseCase mGetLocalSubsUseCase;
    private final AddLocalRootUseCase mAddLocalRootUseCase;
    private final GetLocalRootsUseCase mGetLocalRootsUseCase;
    private final RemoveLocalRootUseCase mRemoveLocalRootUseCase;
    private SettingsContract.View mView;
    private List<FileElement> mLocalFiles;
    private GetLocalSourcesUseCase mGetLocalSourcesUseCase;

    public SettingsPresenter(GetLocalRootsUseCase getLocalRootsUseCase,
                             GetLocalSourcesUseCase getLocalSourcesUseCase,
                             GetLocalSubsUseCase getLocalSubsUseCase,
                             AddLocalRootUseCase addLocalRootUseCase,
                             RemoveLocalRootUseCase removeLocalRootUseCase,
                             SettingsContract.Router router) {
        mGetLocalSourcesUseCase = getLocalSourcesUseCase;
        mGetLocalSubsUseCase = getLocalSubsUseCase;
        mAddLocalRootUseCase = addLocalRootUseCase;
        mGetLocalRootsUseCase = getLocalRootsUseCase;
        mRemoveLocalRootUseCase = removeLocalRootUseCase;
        mRouter = router;

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
        mRemoveLocalRootUseCase.execute(new RemoveLocalRootSubscriber(element), element);
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

    @Override
    public void expandLocal(FileElement element) {
        mGetLocalSubsUseCase.execute(new GetLocalSubsSubscriber(element), element);
    }

    @Override
    public void addLocalRoot(FileElement element) {
        VideoElement root = new VideoElement(element.getFile(), null);
        mAddLocalRootUseCase.execute(new AddLocalRootSubscriber(root), root);
    }

    @Override
    public void retrieveLocalRoots() {
        mGetLocalRootsUseCase.execute(new GetLocalRootsSubscriber());
    }

    private class GetLocalRootsSubscriber extends ResourceObserver<List<VideoElement>> {
        @Override
        public void onNext(List<VideoElement> videoElements) {
            mLocalRoots.clear();
            mLocalRoots.addAll(videoElements);
            if (mView != null) {
                mView.refreshLocalRoots();
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error getting local roots", new Exception(e));
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
            Log.e(TAG, "Error getting local sources", new Exception(e));
        }

        @Override
        public void onComplete() {
            // Nothing to do
        }
    }

    private class GetLocalSubsSubscriber extends ResourceObserver<List<FileElement>> {
        private final FileElement mElement;

        private GetLocalSubsSubscriber(FileElement element) {
            mElement = element;
        }

        @Override
        public void onNext(List<FileElement> fileElements) {
            int position = mLocalFiles.indexOf(mElement) + 1;
            mLocalFiles.addAll(position, fileElements);
            mElement.setExpanded(true);
            if (mView != null) {
                mView.notifyLocalSubsRetrieved(position, fileElements.size());
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error getting local subs", new Exception(e));
        }

        @Override
        public void onComplete() {
            // Nothing to do
        }
    }

    private class AddLocalRootSubscriber extends ResourceObserver<Void> {
        private final VideoElement mElement;

        private AddLocalRootSubscriber(VideoElement element) {
            mElement = element;
        }

        @Override
        public void onNext(Void aVoid) {
            // Nothing to do
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error adding local root", new Exception(e));
        }

        @Override
        public void onComplete() {
            mLocalRoots.add(mElement);
            if (mView != null) {
                mView.refreshLocalRoots();
            }
        }
    }

    private class RemoveLocalRootSubscriber extends ResourceObserver<Void> {
        private final VideoElement mElement;

        private RemoveLocalRootSubscriber(VideoElement element) {
            mElement = element;
        }

        @Override
        public void onNext(Void aVoid) {
            // Nothing to do
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error removing local root", new Exception(e));
        }

        @Override
        public void onComplete() {
            mLocalRoots.remove(mElement);
            if (mView != null) {
                mView.refreshLocalRoots();
            }
        }
    }
}
