package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.domain.repository.LocalBrowsingRepository;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

@PerActivity
public class AddLocalRootUseCase extends UseCase<Void, VideoElement> {
    private final LocalBrowsingRepository mRepository;

    @Inject
    AddLocalRootUseCase(Scheduler postExecutionThread, LocalBrowsingRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<Void> buildObservable(VideoElement element) {
        return mRepository.addLocalRoot(element).flatMap((Function<Long, ObservableSource<Void>>) value -> {
            if (value >= 0) {
                return Observable.empty();
            } else {
                return Observable.error(new Exception("Error adding local root"));
            }
        });
    }
}
