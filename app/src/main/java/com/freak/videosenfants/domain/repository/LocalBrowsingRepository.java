package com.freak.videosenfants.domain.repository;

import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.content.DatabaseContent;
import com.freak.videosenfants.domain.content.PreferencesContent;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

@Singleton
public class LocalBrowsingRepository {

    private final DatabaseContent.LocalDao mLocalDao;

    @Inject
    LocalBrowsingRepository(DatabaseContent db) {
        mLocalDao = db.localDao();
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
