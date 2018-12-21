package com.freak.videosenfants.domain.useCase;

import android.util.Log;

import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.DlnaElement;
import com.freak.videosenfants.domain.repository.UpnpRepository;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

@PerActivity
public class ListUpnpServersUseCase extends UseCase<DlnaElement, Void> {

    private static final String TAG = ListUpnpServersUseCase.class.getSimpleName();

    private final UpnpRepository mRepository;

    @Inject
    public ListUpnpServersUseCase(Scheduler postExecutionThread, UpnpRepository repository) {
        super(postExecutionThread);
        mRepository = repository;
    }

    @Override
    protected Observable<DlnaElement> buildObservable(Void aVoid) {
        return null;
    }

    @Override
    public void execute(ResourceObserver<DlnaElement> useCaseSubscriber, Void aVoid) {
        mSubscription.add(buildUpnpObservable(aVoid)
                .subscribeOn(Schedulers.io())
                .observeOn(mPostExecutionThread)
                .subscribeWith(new UpnpSubscriber(useCaseSubscriber)));
    }

    private Observable<AndroidUpnpService> buildUpnpObservable(Void aVoid) {
        return mRepository.getUpnpService();
    }

    private class UpnpSubscriber extends ResourceObserver<AndroidUpnpService> {
        private final ResourceObserver<DlnaElement> mUseCaseSubscriber;
        private final BrowseRegistryListener mRegistryListener;

        private UpnpSubscriber(ResourceObserver<DlnaElement> useCaseSubscriber) {
            mUseCaseSubscriber = useCaseSubscriber;
            mRegistryListener = new BrowseRegistryListener(mUseCaseSubscriber);
        }

        @Override
        public void onNext(AndroidUpnpService upnpService) {
            // Get ready for future device advertisements
            upnpService.getRegistry().addListener(mRegistryListener);

            // Now add all devices to the list we already know about
            for (Device device : upnpService.getRegistry().getDevices()) {
                mRegistryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            upnpService.getControlPoint().search();
        }

        @Override
        public void onError(Throwable e) {
            mUseCaseSubscriber.onError(e);
        }

        @Override
        public void onComplete() {
            mUseCaseSubscriber.onComplete();
        }
    }

    class BrowseRegistryListener extends DefaultRegistryListener {

        private final ResourceObserver<DlnaElement> mUseCaseSubscriber;

        private BrowseRegistryListener(ResourceObserver<DlnaElement> useCaseSubscriber) {
            mUseCaseSubscriber = useCaseSubscriber;
        }

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        }
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
        }

        void deviceAdded(final Device device) {
            String url;
            if (device.getDetails().getPresentationURI() != null) {
                url = device.getDetails().getPresentationURI().getAuthority();
            } else {
                url = "null";
            }
            String name = device.getDetails().getFriendlyName();
            Log.d(TAG, "Add device " + name + " @ " + url);
            if (device.isFullyHydrated()) {
                for (RemoteService service : (RemoteService[]) device.getServices()) {
                    if (service.getServiceType().getType().equals("ContentDirectory")) {
                        DlnaElement d = new DlnaElement(device, service);
                        mUseCaseSubscriber.onNext(d);
                    }
                }
            }
        }
    }
}
