package com.freak.videosenfants.app.browse.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.core.BaseContract;
import com.freak.videosenfants.app.core.BasePresenter;
import com.freak.videosenfants.elements.browsing.VideoElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BrowseLocalPresenter extends BasePresenter implements BrowseLocalContract.Presenter {
    private static final String TAG = BrowseLocalPresenter.class.getSimpleName();

    private static final String[] TAB_EXTENSIONS = {"avi" , "mkv", "wmv", "mpg", "mpeg", "mp4"};
    private static final Set<String> EXTENSIONS = new HashSet<>(Arrays.asList(TAB_EXTENSIONS));

    private final BrowseLocalContract.Router mRouter;
    private final ArrayList<VideoElement> mItems;

    private ArrayList<VideoElement> mRoots;
    private BrowseLocalContract.View mView;
    private VideoElement mCurrent;

    public BrowseLocalPresenter(BrowseLocalContract.Router router) {
        mRouter = router;

        mItems = new ArrayList<>();
    }

    @Override
    public void subscribe(BaseContract.View view) {
        mView = (BrowseLocalContract.View) view;
        mRoots = getLocalRoots(mView);
        mItems.addAll(mRoots);
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
            mItems.clear();
            for (File file: subFiles) {
                mItems.add(new VideoElement(file.isDirectory(), file.getAbsolutePath(), file.getName(), element, null));
            }
            sortItems();
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

    private static ArrayList<VideoElement> getLocalRoots(BrowseLocalContract.View view) {
        // ToDo to refactor
        Context context = view.getContext();
        ArrayList<VideoElement> result = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int nbRoots = context.getResources().getInteger(R.integer.local_roots_number);
        for (int i = 0 ; i < nbRoots ; i++){
            boolean visible = prefs.getBoolean(context.getString(R.string.key_local_browse) + "_" + i + context.getString(R.string.key_visible), false);
            boolean empty = prefs.getString(context.getString(R.string.key_local_browse) + "_" + i, "").length() == 0;
            if (visible && !empty) {
                File childrenFolder = new File(prefs.getString(context.getString(R.string.key_local_browse) + "_" + i, ""));
                Log.i(TAG, "New root found: " + childrenFolder.getAbsolutePath());
                if (childrenFolder.exists() && childrenFolder.isDirectory()) {
                    Log.i(TAG, "New root added: " + childrenFolder.getAbsolutePath());
                    result.add(new VideoElement(true, childrenFolder.getAbsolutePath(), childrenFolder.getName(), null, context));
                }
            }
        }
        return result;
    }

    private void sortItems() {
        ArrayList<VideoElement> directories = new ArrayList<>();
        ArrayList<VideoElement> files = new ArrayList<>();

        for (int i = 0; i < mItems.size() ; i++) {
            if (mItems.get(i).isDirectory()) {
                directories.add(mItems.get(i));
            } else {
                files.add(mItems.get(i));
            }
        }

        mItems.clear();

        sort(directories);
        mItems.addAll(directories);
        sort(files);
        mItems.addAll(files);
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
}
