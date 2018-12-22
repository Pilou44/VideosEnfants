package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.repository.UpnpBrowsingRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

@PerActivity
public class RemoveUpnpRootUseCase extends UseCase<Void, DlnaElement> {

    private final UpnpBrowsingRepository mRepository;

    @Inject
    public RemoveUpnpRootUseCase(Scheduler postExecutionThread, UpnpBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<Void> buildObservable(DlnaElement element) {
        return mRepository.removeElement(element).flatMap((Function<Integer, ObservableSource<Void>>) value -> {
            if (value > 0) {
                return Observable.empty();
            } else {
                return Observable.error(new Exception());
            }
        });
    }
}
