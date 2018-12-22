package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.repository.LocalBrowsingRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

@PerActivity
public class RemoveLocalRootUseCase extends UseCase<Void, VideoElement> {

    private final LocalBrowsingRepository mRepository;

    @Inject
    public RemoveLocalRootUseCase(Scheduler postExecutionThread, LocalBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<Void> buildObservable(VideoElement videoElement) {
        return mRepository.removeElement(videoElement).flatMap((Function<Integer, ObservableSource<Void>>) value -> {
            if (value > 0) {
                return Observable.empty();
            } else {
                return Observable.error(new Exception());
            }
        });
    }
}
