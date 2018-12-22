package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.repository.UpnpBrowsingRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

@PerActivity
public class GetUpnpRootsUseCase extends UseCase<List<DlnaElement>, Void> {

    private final UpnpBrowsingRepository mRepository;

    @Inject
    public GetUpnpRootsUseCase(Scheduler postExecutionThread, UpnpBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<List<DlnaElement>> buildObservable(Void aVoid) {
        return mRepository.getUpnpRoots();
    }
}
