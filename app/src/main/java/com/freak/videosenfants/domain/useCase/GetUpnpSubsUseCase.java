package com.freak.videosenfants.domain.useCase;

import android.os.Handler;
import android.util.Log;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.repository.UpnpRepository;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

@PerActivity
public class GetUpnpSubsUseCase extends UseCase<List<DlnaElement>, DlnaElement> {

    private static final String TAG = GetUpnpSubsUseCase.class.getSimpleName();

    private final UpnpRepository mRepository;

    @Inject
    public GetUpnpSubsUseCase(Scheduler postExecutionThread, UpnpRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<List<DlnaElement>> buildObservable(DlnaElement dlnaElement) {
        return null;
    }

    @Override
    public void execute(ResourceObserver<List<DlnaElement>> useCaseSubscriber, DlnaElement dlnaElement) {
        mSubscription.add(buildUpnpObservable(dlnaElement)
                .subscribeOn(Schedulers.io())
                .observeOn(mPostExecutionThread)
                .subscribeWith(new UpnpSubscriber(useCaseSubscriber, dlnaElement)));
    }

    private Observable<AndroidUpnpService> buildUpnpObservable(DlnaElement dlnaElement) {
        return mRepository.getUpnpService();
    }

    private class UpnpSubscriber extends ResourceObserver<AndroidUpnpService> {
        private final ResourceObserver<List<DlnaElement>> mUseCaseSubscriber;
        private final Handler mHandler;
        private final DlnaElement mElement;

        private UpnpSubscriber(ResourceObserver<List<DlnaElement>> useCaseSubscriber, DlnaElement element) {
            mUseCaseSubscriber = useCaseSubscriber;
            mHandler = new Handler();
            mElement = element;
        }

        @Override
        public void onNext(AndroidUpnpService androidUpnpService) {
            androidUpnpService.getControlPoint().execute(new Browse(mElement.getService(), mElement.getPath(), BrowseFlag.DIRECT_CHILDREN) {
                @Override
                public void received(ActionInvocation arg0, final DIDLContent didl) {
                    Log.d(TAG, "found " + didl.getContainers().size() + " items.");
                    ArrayList<DlnaElement> mElements = new ArrayList<>();
                    for (int i = 0; i < didl.getContainers().size(); i++) {
                        DlnaElement newElement = new DlnaElement(didl.getContainers().get(i).getTitle(), didl.getContainers().get(i).getId(), mElement);
                        mElements.add(newElement);
                    }
                    mHandler.post(() -> mUseCaseSubscriber.onNext(mElements));
                }

                @Override
                public void updateStatus(Status status) {

                }

                @Override
                public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                }
            });
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }
}
