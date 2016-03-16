package com.freak.videosenfants;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
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

public class BrowseDlnaActivity extends BrowseActivity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseDlnaActivity.class.getSimpleName();

    private ListView mListView;
    private Handler mHandler;
    private RemoteService mService;
    private BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();
    private AndroidUpnpService mUpnpService;
    private VideoElementAdapter mAdapter;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            if (DEBUG)
                Log.i(TAG, "Service connected");

            mUpnpService = (AndroidUpnpService) service;

            // Clear the list
            mAdapter.clear();

            // Get ready for future device advertisements
            mUpnpService.getRegistry().addListener(mRegistryListener);

            // Now add all devices to the list we already know about
            for (Device device : mUpnpService.getRegistry().getDevices()) {
                mRegistryListener.deviceAdded(device);
            }

            // Search asynchronously for all devices, they will respond soon
            mRoot = "33$14";
            String udn = "0011324b-22b7-0011-b722-b7224b321100";
            String url = "http://192.168.1.63:50001/desc/device.xml";
            int maxAge = 1900;

            RetrieveDeviceThread thread = new RetrieveDeviceThread(mUpnpService, udn, url, maxAge);
            thread.start();

        }

        public void onServiceDisconnected(ComponentName className) {
            mUpnpService = null;
        }
    };
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_dlna);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowseDlnaActivity.this.onBackPressed();
            }
        });

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );
        // Now you can enable logging as needed for various categories of Cling:
        // Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);

        mHandler = new Handler();

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);

        mAdapter = new VideoElementAdapter(this);
        mAdapter.setNotifyOnChange(false);
        mListView.setAdapter(mAdapter);

        if (DEBUG)
            Log.i(TAG, "Bind service");

        // This will start the UPnP service if it wasn't already started
        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        mDialog = ProgressDialog.show(this, "Recherche du serveur", "Merci de patienter...", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpnpService != null) {
            mUpnpService.getRegistry().removeListener(mRegistryListener);
        }
        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(mServiceConnection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final VideoElement element = (VideoElement) parent.getItemAtPosition(position);

        if (element.isDirectory()) {
            if (DEBUG)
                Log.i(TAG, "Directory clicked");
            mUpnpService.getControlPoint().execute(new Browse(mService, element.getPath(), BrowseFlag.DIRECT_CHILDREN) {
                @Override
                public void received(ActionInvocation arg0,
                                     DIDLContent didl) {
                    parseAndUpdate(didl);
                    mCurrent = element;
                }

                @Override
                public void updateStatus(Status status) {

                }

                @Override
                public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                }
            });
        }
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.getPath()));
            intent.setDataAndType(Uri.parse(element.getPath()), "video/*");
            startActivity(intent);
        }
    }

    protected void parseAndUpdate(final VideoElement element) {
        mUpnpService.getControlPoint().execute(new Browse(mService, element.getPath(), BrowseFlag.DIRECT_CHILDREN) {
            @Override
            public void received(ActionInvocation arg0,
                                 DIDLContent didl) {
                parseAndUpdate(didl);
                mCurrent = element;
            }

            @Override
            public void updateStatus(Status status) {

            }

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
            }
        });
    }

    private void parseAndUpdate(final DIDLContent didl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();

                if (DEBUG)
                    Log.i(TAG, "found " + didl.getContainers().size() + " items.");
                for (int i = 0; i < didl.getContainers().size(); i++) {
                    VideoElement element = new VideoElement(
                            true,
                            generateScreenshot(null, didl.getContainers().get(i).getTitle(), true),
                            didl.getContainers().get(i).getId(),
                            didl.getContainers().get(i).getTitle(),
                            mCurrent);
                    mAdapter.add(element);
                }

                if (DEBUG)
                    Log.i(TAG, "found " + didl.getItems().size() + " items.");
                for (int i = 0; i < didl.getItems().size(); i++) {
                    mAdapter.add(new VideoElement(
                            false,
                            generateScreenshot(didl.getItems().get(i).getResources().get(0).getValue(), didl.getItems().get(i).getTitle(), false),
                            didl.getItems().get(i).getResources().get(0).getValue(),
                            didl.getItems().get(i).getTitle(),
                            mCurrent));
                }
                mAdapter.notifyDataSetChanged();
                mListView.setSelectionAfterHeaderView();
            }
        });
    }

    protected class BrowseRegistryListener extends DefaultRegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        }

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
            if (device.getType().getType().equals("MediaServer")) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (device.isFullyHydrated()) {
                            for (RemoteService service : (RemoteService[]) device.getServices()) {
                                if (service.getServiceType().getType().equals("ContentDirectory")) {
                                    mService = service;

                                    if (DEBUG)
                                        Log.i(TAG, "ContentDirectory found");

                                    mUpnpService.getControlPoint().execute(new Browse(service, mRoot, BrowseFlag.DIRECT_CHILDREN) {
                                        @Override
                                        public void received(ActionInvocation arg0,
                                                             DIDLContent didl) {
                                            parseAndUpdate(didl);
                                            mCurrent = new VideoElement(true, null, mRoot, "Root", null);
                                        }

                                        @Override
                                        public void updateStatus(Status status) {

                                        }

                                        @Override
                                        public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
                                        }
                                    });
                                    mDialog.dismiss();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

}
