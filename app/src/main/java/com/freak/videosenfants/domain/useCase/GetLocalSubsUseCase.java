package com.freak.videosenfants.domain.useCase;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.FileElement;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;

@PerActivity
public class GetLocalSubsUseCase extends UseCase<List<FileElement>, FileElement> {

    @Inject
    public GetLocalSubsUseCase(Scheduler postExecutionThread) {
        super(postExecutionThread);
    }

    @Override
    protected Observable<List<FileElement>> buildObservable(FileElement parent) {
        return Observable.defer(() -> {
            File[] subFiles = parent.getFile().listFiles(File::isDirectory);
            List<FileElement> result = new ArrayList<>();
            for (File file:subFiles) {
                result.add(new FileElement(file, parent.getIndent() + 1));
            }
            return Observable.just(result);
        });
    }
}
