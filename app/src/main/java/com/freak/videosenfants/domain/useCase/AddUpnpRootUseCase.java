package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.repository.UpnpBrowsingRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class AddUpnpRootUseCase  extends UseCase<Void, DlnaElement> {
    private final UpnpBrowsingRepository mRepository;

    @Inject
    AddUpnpRootUseCase(Scheduler postExecutionThread, UpnpBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<Void> buildObservable(DlnaElement element) {
        return mRepository.addUpnpRoot(element).flatMap((Function<Long, ObservableSource<Void>>) value -> {
            if (value >= 0) {
                return Observable.empty();
            } else {
                return Observable.error(new Exception("Error adding local root"));
            }
        });
    }
}
