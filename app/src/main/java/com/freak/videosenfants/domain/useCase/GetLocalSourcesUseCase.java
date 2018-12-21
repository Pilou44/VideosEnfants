package com.freak.videosenfants.domain.useCase;

import android.content.Context;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.FileElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

@PerActivity
public class GetLocalSourcesUseCase extends UseCase<List<FileElement>, Void> {

    private final Context mContext;

    @Inject
    GetLocalSourcesUseCase(Scheduler postExecutionThread, Context context) {
        super(postExecutionThread);
        mContext = context;
    }

    @Override
    protected Observable<List<FileElement>> buildObservable(Void aVoid) {
        return Observable.defer(() -> {
            List<FileElement> sources = new ArrayList<>();
            File[] files = mContext.getExternalFilesDirs(null);
            for (File file : files) {
                String path = file.getAbsolutePath();
                file = new File(path.replaceAll("/Android/data/" + mContext.getPackageName() + "/files", ""));
                sources.add(new FileElement(file, file.getPath()));
            }
            return Observable.just(sources);
        });
    }
}
