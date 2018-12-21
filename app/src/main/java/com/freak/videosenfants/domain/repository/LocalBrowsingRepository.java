package com.freak.videosenfants.domain.repository;

import com.freak.videosenfants.domain.content.PreferencesContent;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LocalBrowsingRepository {

    private static final String KEY_LOCAL_BROWSE = "local_browse";
    private static final String KEY_VISIBLE = "_visible";

    private PreferencesContent mPreferences;

    @Inject
    LocalBrowsingRepository(PreferencesContent preferences) {
        mPreferences = preferences;
    }

    public Observable<Boolean> isLocalRootVisible(int index) {
        return Observable.just(mPreferences.getBoolean(KEY_LOCAL_BROWSE + "_" + index + KEY_VISIBLE, false));
    }

    public Observable<String> getLocalRoot(int index) {
        return Observable.just(mPreferences.getString(KEY_LOCAL_BROWSE + "_" + index, ""));
    }
}
