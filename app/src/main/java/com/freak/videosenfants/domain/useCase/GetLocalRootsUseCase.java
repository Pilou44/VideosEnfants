package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.repository.LocalBrowsingRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

@PerActivity
public class GetLocalRootsUseCase extends UseCase<List<VideoElement>, Void> {

    private final LocalBrowsingRepository mRepository;

    @Inject
    public GetLocalRootsUseCase(Scheduler postExecutionThread, LocalBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<List<VideoElement>> buildObservable(Void aVoid) {
        return mRepository.getLocalRoots();
    }
}
