package com.freak.videosenfants.app.settings;

import android.util.Log;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.domain.bean.BaseElement;
import com.freak.videosenfants.domain.bean.BrowsableElement;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.bean.FileElement;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.useCase.AddLocalRootUseCase;
import com.freak.videosenfants.domain.useCase.AddUpnpRootUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalSourcesUseCase;
import com.freak.videosenfants.domain.useCase.GetLocalSubsUseCase;
import com.freak.videosenfants.domain.useCase.GetUpnpRootsUseCase;
import com.freak.videosenfants.domain.useCase.GetUpnpSubsUseCase;
import com.freak.videosenfants.domain.useCase.ListUpnpServersUseCase;
import com.freak.videosenfants.domain.useCase.RemoveLocalRootUseCase;
import com.freak.videosenfants.domain.useCase.RemoveUpnpRootUseCase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.ResourceObserver;

public class SettingsPresenter extends BasePresenter implements SettingsContract.Presenter {
    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final ArrayList<BaseElement> mLocalRoots;
    private final ArrayList<BaseElement> mUpnpRoots;
    private final List<BrowsableElement> mLocalFiles;
    private final List<BrowsableElement> mUpnpFiles;
    private final SettingsContract.Router mRouter;
    private final GetLocalSubsUseCase mGetLocalSubsUseCase;
    private final AddLocalRootUseCase mAddLocalRootUseCase;
    private final GetLocalRootsUseCase mGetLocalRootsUseCase;
    private final RemoveLocalRootUseCase mRemoveLocalRootUseCase;
    private final GetUpnpRootsUseCase mGetUpnpRootsUseCase;
    private final RemoveUpnpRootUseCase mRemoveUpnpRootUseCase;
    private final ListUpnpServersUseCase mListUpnpServersUseCase;
    private final GetUpnpSubsUseCase mGetUpnpSubsUseCase;
    private final AddUpnpRootUseCase mAddUpnpRootUseCase;
    private SettingsContract.View mView;
    private GetLocalSourcesUseCase mGetLocalSourcesUseCase;

    public SettingsPresenter(GetLocalRootsUseCase getLocalRootsUseCase,
                             GetUpnpRootsUseCase geUpnpRootsUseCase,
                             GetLocalSourcesUseCase getLocalSourcesUseCase,
                             GetLocalSubsUseCase getLocalSubsUseCase,
                             AddLocalRootUseCase addLocalRootUseCase,
                             RemoveLocalRootUseCase removeLocalRootUseCase,
                             RemoveUpnpRootUseCase removeUpnpRootUseCase,
                             ListUpnpServersUseCase listUpnpServersUseCase,
                             GetUpnpSubsUseCase getUpnpSubsUseCase,
                             AddUpnpRootUseCase addUpnpRootUseCase,
                             SettingsContract.Router router) {
        mGetLocalSourcesUseCase = getLocalSourcesUseCase;
        mGetUpnpRootsUseCase = geUpnpRootsUseCase;
        mGetLocalSubsUseCase = getLocalSubsUseCase;
        mAddLocalRootUseCase = addLocalRootUseCase;
        mGetLocalRootsUseCase = getLocalRootsUseCase;
        mRemoveLocalRootUseCase = removeLocalRootUseCase;
        mRemoveUpnpRootUseCase = removeUpnpRootUseCase;
        mListUpnpServersUseCase = listUpnpServersUseCase;
        mGetUpnpSubsUseCase = getUpnpSubsUseCase;
        mAddUpnpRootUseCase = addUpnpRootUseCase;
        mRouter = router;

        mLocalRoots = new ArrayList<>();
        mUpnpRoots = new ArrayList<>();
        mLocalFiles = new ArrayList<>();
        mUpnpFiles = new ArrayList<>();
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
    public List<BaseElement> getRoots(int type) {
        if (type == TYPE_LOCAL) {
            return mLocalRoots;
        } else if (type == TYPE_UPNP) {
            return mUpnpRoots;
        } else {
            return null;
        }
    }

    @Override
    public void removeRoot(int type, BaseElement element) {
        if (type == TYPE_LOCAL) {
            mRemoveLocalRootUseCase.execute(new RemoveLocalRootSubscriber((VideoElement) element), (VideoElement) element);
        } else if (type == TYPE_UPNP) {
            mRemoveUpnpRootUseCase.execute(new RemoveUpnpRootSubscriber((DlnaElement) element), (DlnaElement) element);
        }
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
    public List<BrowsableElement> getFiles(int type) {
        if (type == TYPE_LOCAL) {
            return mLocalFiles;
        } else if (type == TYPE_UPNP) {
            return mUpnpFiles;
        } else {
            return null;
        }
    }

    @Override
    public void retrieveSources(int type) {
        if (type == TYPE_LOCAL) {
            mLocalFiles.clear();
            mGetLocalSourcesUseCase.execute(new GetLocalSourcesSubscriber());
        } else if (type == TYPE_UPNP) {
            mUpnpFiles.clear();
            mListUpnpServersUseCase.execute(new ListUpnpServersSubscriber());
        }
    }

    @Override
    public void expand(BrowsableElement element, int type) {
        if (type == TYPE_LOCAL) {
            mGetLocalSubsUseCase.execute(new GetLocalSubsSubscriber((FileElement) element), (FileElement) element);
        } else if (type == TYPE_UPNP) {
            mGetUpnpSubsUseCase.execute(new GetUpnpSubsSubscriber((DlnaElement) element), (DlnaElement) element);
        }
    }

    @Override
    public void addRoot(BrowsableElement element, int type) {
        if (type == TYPE_LOCAL) {
            VideoElement root = new VideoElement(((FileElement) element).getFile(), null);
            mAddLocalRootUseCase.execute(new AddLocalRootSubscriber(root), root);
        } else if (type == TYPE_UPNP) {
            mAddUpnpRootUseCase.execute(new AddUpnpRootSubscriber((DlnaElement) element), (DlnaElement) element);
        }
    }

    @Override
    public void retrieveRoots(int type) {
        if (type == TYPE_LOCAL) {
            mGetLocalRootsUseCase.execute(new GetLocalRootsSubscriber());
        } else if (type == TYPE_UPNP) {
            mGetUpnpRootsUseCase.execute(new GetUpnpRootsSubscriber());
        }
    }

    private class GetLocalRootsSubscriber extends ResourceObserver<List<VideoElement>> {
        @Override
        public void onNext(List<VideoElement> videoElements) {
            mLocalRoots.clear();
            mLocalRoots.addAll(videoElements);
            if (mView != null) {
                mView.refreshRoots();
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
                mView.refreshSources();
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

    private class ListUpnpServersSubscriber extends ResourceObserver<DlnaElement> {
        @Override
        public void onNext(DlnaElement server) {
            mUpnpFiles.add(server);
            if (mView != null) {
                mView.refreshSources();
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
                mView.notifySubsRetrieved(position, fileElements.size());
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

    private class GetUpnpSubsSubscriber extends ResourceObserver<List<DlnaElement>> {
        private final DlnaElement mElement;

        private GetUpnpSubsSubscriber(DlnaElement element) {
            mElement = element;
        }

        @Override
        public void onNext(List<DlnaElement> elements) {
            int position = mUpnpFiles.indexOf(mElement) + 1;
            mUpnpFiles.addAll(position, elements);
            mElement.setExpanded(true);
            if (mView != null) {
                mView.notifySubsRetrieved(position, elements.size());
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error getting upnp subs", new Exception(e));
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
                mView.refreshRoots();
            }
        }
    }

    private class AddUpnpRootSubscriber extends ResourceObserver<Void> {
        private final DlnaElement mElement;

        private AddUpnpRootSubscriber(DlnaElement element) {
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
            mUpnpRoots.add(mElement);
            if (mView != null) {
                mView.refreshRoots();
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
                mView.refreshRoots();
            }
        }
    }

    private class RemoveUpnpRootSubscriber extends ResourceObserver<Void> {
        private final DlnaElement mElement;

        private RemoveUpnpRootSubscriber(DlnaElement element) {
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
            mUpnpRoots.remove(mElement);
            if (mView != null) {
                mView.refreshRoots();
            }
        }
    }

    private class GetUpnpRootsSubscriber extends ResourceObserver<List<DlnaElement>> {
        @Override
        public void onNext(List<DlnaElement> elements) {
            mUpnpRoots.clear();
            mUpnpRoots.addAll(elements);
            if (mView != null) {
                mView.refreshRoots();
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
}
