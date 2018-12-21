package com.freak.videosenfants.domain.repository;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.freak.videosenfants.app.settings.dlna.BrowseDlnaPreference;
import com.freak.videosenfants.domain.bean.DlnaElement;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

@Singleton
public class UpnpRepository {

    private static final String TAG = UpnpRepository.class.getSimpleName();

    private AndroidUpnpService mUpnpService;

    private final ServiceConnection mServiceConnection;

    @Inject
    UpnpRepository(Context context) {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.i(TAG, "Service connected");

                mUpnpService = (AndroidUpnpService) service;

                // Get ready for future device advertisements
                //mUpnpService.getRegistry().addListener(registryListener);

                // Now add all devices to the list we already know about
                //for (Device device : mUpnpService.getRegistry().getDevices()) {
                //    registryListener.deviceAdded(device);
                //}

                // Search asynchronously for all devices, they will respond soon
                //mUpnpService.getControlPoint().search();
            }

            public void onServiceDisconnected(ComponentName className) {
                Log.i(TAG, "Service disconnected");
                mUpnpService = null;
            }
        };

        context.bindService(
                new Intent(context, AndroidUpnpServiceImpl.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    public Observable<AndroidUpnpService> getUpnpService() {
        return Observable.defer(() -> {
            while (mUpnpService == null) {
                Thread.sleep(100);
            }
            return Observable.just(mUpnpService);
        });
    }
}
