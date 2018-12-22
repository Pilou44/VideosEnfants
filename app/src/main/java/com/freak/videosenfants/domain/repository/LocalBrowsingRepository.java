package com.freak.videosenfants.domain.repository;

import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.content.DatabaseContent;
import com.freak.videosenfants.domain.content.PreferencesContent;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class LocalBrowsingRepository {

    private static final String KEY_LOCAL_BROWSE = "local_browse";
    private static final String KEY_VISIBLE = "_visible";

    private final DatabaseContent.LocalDao mLocalDao;
    private final PreferencesContent mPreferences;

    @Inject
    LocalBrowsingRepository(PreferencesContent preferences, DatabaseContent db) {
        mPreferences = preferences;
        mLocalDao = db.localDao();
    }

    public Observable<Boolean> isLocalRootVisible(int index) {
        return Observable.just(mPreferences.getBoolean(KEY_LOCAL_BROWSE + "_" + index + KEY_VISIBLE, false));
    }

    public Observable<String> getLocalRoot(int index) {
        return Observable.just(mPreferences.getString(KEY_LOCAL_BROWSE + "_" + index, ""));
    }

    public Observable<Long> addLocalRoot(VideoElement element) {
        return Observable.defer(() -> Observable.just(mLocalDao.insert(element)));
    }

    public Observable<List<VideoElement>> getLocalRoots() {
        return mLocalDao.getAll().toObservable();
    }

    public Observable<Integer> removeElement(VideoElement element) {
        return Observable.defer(() -> Observable.just(mLocalDao.delete(element)));
    }
}
