package com.freak.videosenfants.domain.repository;

import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.content.DatabaseContent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class UpnpBrowsingRepository {

    private final DatabaseContent.DlnaDao mDlnaDao;

    @Inject
    UpnpBrowsingRepository(DatabaseContent db) {
        mDlnaDao = db.dlnaDao();
    }

    public Observable<Long> addUpnpRoot(DlnaElement element) {
        return Observable.defer(() -> Observable.just(mDlnaDao.insert(element)));
    }

    public Observable<List<DlnaElement>> getUpnpRoots() {
        return mDlnaDao.getAll().toObservable();
    }

    public Observable<Integer> removeElement(DlnaElement element) {
        return Observable.defer(() -> Observable.just(mDlnaDao.delete(element)));
    }
}
