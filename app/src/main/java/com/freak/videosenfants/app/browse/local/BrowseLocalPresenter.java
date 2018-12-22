package com.freak.videosenfants.app.browse.local;

import android.net.Uri;
import android.util.Log;

import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.useCase.GetLocalRootsUseCase;
import com.freak.videosenfants.domain.useCase.GetThumbnailUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.observers.ResourceObserver;

public class BrowseLocalPresenter extends BasePresenter implements BrowseLocalContract.Presenter {
    private static final String TAG = BrowseLocalPresenter.class.getSimpleName();

    private static final String[] TAB_EXTENSIONS = {"avi" , "mkv", "wmv", "mpg", "mpeg", "mp4"};
    private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(TAB_EXTENSIONS));

    private final BrowseLocalContract.Router mRouter;
    private final ArrayList<VideoElement> mItems;
    private final GetThumbnailUseCase mGetThumbnailUseCase;
    private final GetLocalRootsUseCase mGetLocalRootsUseCase;

    private List<VideoElement> mRoots;
    private BrowseLocalContract.View mView;
    private VideoElement mCurrent;

    public BrowseLocalPresenter(GetLocalRootsUseCase getLocalRootsUseCase,
                                GetThumbnailUseCase getThumbnailUseCase,
                                BrowseLocalContract.Router router) {
        mGetLocalRootsUseCase = getLocalRootsUseCase;
        mGetThumbnailUseCase = getThumbnailUseCase;
        mRouter = router;

        mItems = new ArrayList<>();
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (BrowseLocalContract.View) view;
    }

    @Override
    public void unsubscribe(BaseContract.View view) {
        if (mView.equals(view)) {
            mView = null;
        }
    }

    @Override
    public void playVideo(Uri videoUri) {
        mRouter.playVideoWithAndroid(mView, videoUri);
    }

    @Override
    public List<VideoElement> getCurrentItems() {
        return mItems;
    }

    @Override
    public VideoElement getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void browseLocal(VideoElement element) {
        if (element.isDirectory()) {
            mCurrent = element;
            mItems.clear();
            mItems.addAll(getContent(element, element));
            mView.notifyElementsUpdated();
        }
    }

    @Override
    public void goBack() {
        if (mCurrent == null) {
            mRouter.goStartActivity(mView);
        } else if (mCurrent.getParent() == null) {
            mItems.clear();
            mItems.addAll(mRoots);
            mCurrent = null;
            mView.notifyElementsUpdated();
        } else {
            browseLocal(mCurrent.getParent());
        }
    }

    @Override
    public void getImageUri(VideoElement element) {
        mGetThumbnailUseCase.execute(new GetThumbnailSubscriber(element), element);

    }

    @Override
    public void retrieveLocalRoots() {
        mGetLocalRootsUseCase.execute(new GetLocalRootsSubscriber());
    }

    private List<VideoElement> sortItems(File[] files, VideoElement parent) {
        ArrayList<VideoElement> directories = new ArrayList<>();
        ArrayList<VideoElement> videos = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                directories.add(new VideoElement(file, parent));
            } else {
                videos.add(new VideoElement(file, parent));
            }
        }

        sort(directories);
        ArrayList<VideoElement> result = new ArrayList<>(directories);
        sort(videos);
        result.addAll(videos);

        return result;
    }

    private void sort (ArrayList<VideoElement> files) {
        int longueur = files.size();
        VideoElement tampon;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (files.get(i).getName().compareToIgnoreCase(files.get((i+1)).getName()) > 0) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = files.get(i);
                    files.set(i, files.get(i + 1));
                    files.set(i+1, tampon);
                    permut = true;
                }
            }
        } while (permut);
    }

    private class GetLocalRootsSubscriber extends ResourceObserver<List<VideoElement>> {
        @Override
        public void onNext(List<VideoElement> elements) {
            if (elements.size() == 1) {
                VideoElement element = elements.get(0);
                mRoots = getContent(element, null);
            } else {
                mRoots = elements;
            }
            mItems.addAll(mRoots);

            if (mView != null) {
                mView.notifyElementsUpdated();
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

    private List<VideoElement> getContent(VideoElement element, VideoElement parent) {
        File[] subFiles = new File(element.getPath()).listFiles(pathname -> {
            if (pathname.isDirectory()) {
                return true;
            }
            else {
                try {
                    String extension = pathname.getName().substring(pathname.getName().lastIndexOf(".") + 1).toLowerCase();
                    return EXTENSIONS.contains(extension);
                }
                catch (Exception e) {
                    return false;
                }
            }
        });
        return sortItems(subFiles, parent);
    }

    private class GetThumbnailSubscriber extends ResourceObserver<Uri> {
        private final VideoElement mElement;

        private GetThumbnailSubscriber(VideoElement element) {
            mElement = element;
        }

        @Override
        public void onNext(Uri uri) {
            if (mView != null) {
                mView.showElementThumbnail(mElement, uri);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Error getting thumbnail", new Exception(e));
            if (mView != null) {
                mView.showElementThumbnail(mElement, null);
            }
        }

        @Override
        public void onComplete() {

        }
    }
}
