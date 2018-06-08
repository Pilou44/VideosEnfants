package com.freak.videosenfants.elements.preferences;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freak.videosenfants.R;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;

import java.util.Vector;

public class BrowseDlnaPreference extends BrowsePreference implements AdapterView.OnItemClickListener {
    private static final boolean DEBUG = true;
    private static final String TAG = BrowseDlnaPreference.class.getSimpleName();

    private ListView mListView;
    private final Handler mHandler;
    private DlnaElement mSelectedElement;
    private Vector<DlnaElement> mAllFiles;
    private DlnaAdapter mAdapter;
    private AndroidUpnpService mUpnpService;
    private final BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (DEBUG)
                Log.i(TAG, "Service connected");

            mUpnpService = (AndroidUpnpService) service;

            // Get ready for future device advertisements
            mUpnpService.getRegistry().addListener(mRegistryListener);

            // Now add all devices to the list we already know about
            for (Device device : mUpnpService.getRegistry().getDevices()) {
                mRegistryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            mUpnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            if (DEBUG)
                Log.i(TAG, "Service disconnected");
            mUpnpService = null;
        }
    };

    @SuppressWarnings("WeakerAccess")
    public BrowseDlnaPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(R.layout.dlna_preference_dialog);
        mHandler = new Handler();
    }

    @SuppressWarnings("WeakerAccess")
    public BrowseDlnaPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dlna_preference_dialog);
        mHandler = new Handler();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mSelectedElement = null;

        mAllFiles = new Vector<>();
        mAdapter = new DlnaAdapter(getContext(), mAllFiles);
        mAdapter.setNotifyOnChange(false);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        if (DEBUG)
            Log.i(TAG, "Bind service");

        // This will start the UPnP service if it wasn't already started
        getContext().bindService(
                new Intent(getContext(), AndroidUpnpServiceImpl.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && mSelectedElement != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            if (DEBUG) {
                Log.i(TAG, "Add server");
                Log.i(TAG, "URL: " + mSelectedElement.getUrl());
                Log.i(TAG, "UDN: " + mSelectedElement.getUdn());
                Log.i(TAG, "Path: " + mSelectedElement.getPath());
                Log.i(TAG, "Max age: " + mSelectedElement.getMaxAge());
            }
            editor.putString(this.getKey(), mSelectedElement.getName() + " on " + mSelectedElement.getUrl());
            editor.putString(this.getKey() + getContext().getString(R.string.key_udn), mSelectedElement.getUdn());
            editor.putString(this.getKey() + getContext().getString(R.string.key_url), mSelectedElement.getUrl());
            editor.putString(this.getKey() + getContext().getString(R.string.key_path), mSelectedElement.getPath());
            editor.putInt(this.getKey() + getContext().getString(R.string.key_max_age), mSelectedElement.getMaxAge());
            editor.apply();
            this.notifyChanged();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final DlnaElement element = (DlnaElement)mListView.getItemAtPosition(position);
        if (!element.isExpanded()) {
            mUpnpService.getControlPoint().execute(new Browse(element.getService(), element.getPath(), BrowseFlag.DIRECT_CHILDREN) {
                @Override
                public void received(ActionInvocation arg0,
                                     final DIDLContent didl) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (DEBUG)
                                Log.i(TAG, "found " + didl.getContainers().size() + " items.");
                            for (int i = 0; i < didl.getContainers().size(); i++) {
                                DlnaElement newElement = new DlnaElement(didl.getContainers().get(i).getTitle(), didl.getContainers().get(i).getId(), element);
                                mAllFiles.insertElementAt(newElement, position + i + 1);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void updateStatus(Status status) {

                }

                @Override
                public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                }
            });
            element.setExpanded(true);
        }

        mAdapter.setSelectedElement(position);
        mAdapter.notifyDataSetChanged();
        mSelectedElement = element;
    }

    class BrowseRegistryListener extends DefaultRegistryListener {

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

        public void deviceAdded(final Device device) {
            String url;
            if (device.getDetails().getPresentationURI() != null) {
                url = device.getDetails().getPresentationURI().getAuthority();
            } else {
                url = "null";
            }
            String name = device.getDetails().getFriendlyName();
            Log.d(TAG, "Add device " + name + " @ " + url);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (device.isFullyHydrated()) {
                        for (RemoteService service : (RemoteService[]) device.getServices()) {
                            if (service.getServiceType().getType().equals("ContentDirectory")) {
                                DlnaElement d = new DlnaElement(device, service);
                                int position = mAdapter.getPosition(d);
                                if (position >= 0) {
                                    // Device already in the list, re-set new value at same position
                                    mAllFiles.remove(d);
                                    mAllFiles.insertElementAt(d, position);
                                } else {
                                    mAllFiles.add(d);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }
}
