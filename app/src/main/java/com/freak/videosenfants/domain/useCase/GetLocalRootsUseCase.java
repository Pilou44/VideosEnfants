package com.freak.videosenfants.domain.useCase;

import android.content.Context;

import com.freak.videosenfants.R;
import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.repository.LocalBrowsingRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

@PerActivity
public class GetLocalRootsUseCase extends UseCase<List<VideoElement>, Void> {

    private final LocalBrowsingRepository mRepository;
    private final Context mContext;

    @Inject
    public GetLocalRootsUseCase(Scheduler postExecutionThread, LocalBrowsingRepository repository, Context context) {
        super(postExecutionThread);
        mRepository = repository;
        mContext = context;
    }

    @Override
    protected Observable<List<VideoElement>> buildObservable(Void aVoid) {
        return mRepository.getLocalRoots();
        /*return Observable.defer(() -> {
            ArrayList<VideoElement> result = new ArrayList<>();
            int nbRoots = mContext.getResources().getInteger(R.integer.local_roots_number);
            for (int i = 0 ; i < nbRoots ; i++){
                boolean visible = mRepository.isLocalRootVisible(i).blockingFirst();
                String path = mRepository.getLocalRoot(i).blockingFirst();
                if (visible && !path.isEmpty()) {
                    File childrenFolder = new File(path);
                    if (childrenFolder.exists() && childrenFolder.isDirectory()) {;
                        result.add(new VideoElement(childrenFolder, null));
                    }
                }
            }
            return Observable.just(result);
        });*/
    }
}
