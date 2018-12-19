package com.freak.videosenfants.domain.useCase;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class UseCase<T, Params> {

    final Scheduler mPostExecutionThread;

    CompositeDisposable mSubscription = new CompositeDisposable();

    public UseCase(Scheduler postExecutionThread) {
        mPostExecutionThread = postExecutionThread;
    }

    /**
     * Builds an {@link Observable} which will be used when executing the current {@link UseCase}.
     */
    protected abstract Observable<T> buildObservable(Params params);

    /**
     * Executes the current use case.
     *
     * @param useCaseSubscriber The guy who will be listen to the observable build
     *                          with {@link #buildObservable(Params params)}.
     */
    public void execute(ResourceObserver<T> useCaseSubscriber, Params params) {
        mSubscription.add(buildObservable(params)
                .subscribeOn(Schedulers.io())
                .observeOn(mPostExecutionThread)
                .subscribeWith(useCaseSubscriber));
    }

    public void execute(ResourceObserver<T> useCaseSubscriber) {
        execute(useCaseSubscriber, null);
    }

    /**
     * Unsubscribes from current {@link io.reactivex.disposables.Disposable}.
     */
    public void unsubscribe() {
        if (!mSubscription.isDisposed()) {
            mSubscription.dispose();
        }
    }
}
