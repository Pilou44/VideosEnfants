package com.freak.videosenfants;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.controlpoint.event.Search;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.Res;

import java.io.File;

public class BrowseDlnaActivity extends BrowseActivity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseDlnaActivity.class.getSimpleName();

    private ListView mListView;
    private Handler mHandler;
    private RemoteService mService;

    private BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();
    private AndroidUpnpService mUpnpService;

    //private ArrayAdapter<DeviceDisplay> listAdapter;
    //private ArrayAdapter<VideoElement> listAdapter;
    private VideoElementAdapter mAdapter;
    protected String mRoot = "33$14";
    //private String mCurrentDir;
    //private VideoElement mCurrent;


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
            mUpnpService.getControlPoint().search(new UDNHeader(new UDN("0011324b-22b7-0011-b722-b7224b321100")));
        }

        public void onServiceDisconnected(ComponentName className) {
            mUpnpService = null;
        }
    };

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        //listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        //mListView.setAdapter(listAdapter);

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
                Log.i(TAG, "found " + didl.getContainers().size() + " items.");
                for (int i = 0; i < didl.getContainers().size(); i++) {
                    Log.i(TAG, didl.getContainers().get(i).getTitle() + ", id = " + didl.getContainers().get(i).getId());
                    VideoElement element = new VideoElement(
                            true,
                            generateScreenshot(null, didl.getContainers().get(i).getTitle(), true),
                            didl.getContainers().get(i).getId(),
                            didl.getContainers().get(i).getTitle(),
                            mCurrent);
                    Log.i(TAG, "Add to adapter");
                    mAdapter.add(element);
                }
                Log.i(TAG, "found " + didl.getItems().size() + " items.");
                for (int i = 0; i < didl.getItems().size(); i++) {
                    /*for (final Res resource : didl.getItems().get(i).getResources()) {
                        Log.i(TAG, resource.getValue());
                    }*/
                    Log.i(TAG, didl.getItems().get(i).getTitle() + ", id = " + didl.getItems().get(i).getResources().get(0).getValue());
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

        /* Discovery performance optimization for very slow Android devices! */
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            BrowseDlnaActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': "
                                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
            deviceRemoved(device);
        }
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            if (device.getType().getType().equals("MediaServer")) {
                Log.i(TAG, device.getDisplayString());
                Log.i(TAG, device.getIdentity().getUdn().getIdentifierString());

                /*runOnUiThread(new Runnable() {
                    public void run() {
                        DeviceDisplay d = new DeviceDisplay(device);
                        int position = listAdapter.getPosition(d);
                        if (position >= 0) {
                            // Device already in the list, re-set new value at same position
                            listAdapter.remove(d);
                            listAdapter.insert(d, position);
                        } else {
                            listAdapter.add(d);
                        }
                    }
                });*/
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (device.isFullyHydrated()) {
                            for (RemoteService service : (RemoteService[]) device.getServices()) {
                                if (service.getServiceType().getType().equals("ContentDirectory")) {
                                    mService = service;
                                    Log.i(TAG, "ContentDirectory found");
                                    Log.i(TAG, "service action = " + service.getAction("Browse"));
                                    for (int i = 0; i < service.getActions().length; i++) {
                                        Log.i(TAG, "service action = " + service.getActions()[i]);
                                    }
                                    mUpnpService.getControlPoint().execute(new Browse(service, mRoot, BrowseFlag.DIRECT_CHILDREN) {
                                        @Override
                                        public void received(ActionInvocation arg0,
                                                             DIDLContent didl) {
                                            /*Log.i(TAG, "found " + didl.getContainers().size() + " items.");
                                            for (int i = 0; i < didl.getContainers().size(); i++) {
                                                Log.i(TAG, didl.getContainers().get(i).getTitle() + ", id = " + didl.getContainers().get(i).getId());
                                                VideoElement element = new VideoElement(true, null, didl.getContainers().get(i).getId(), didl.getContainers().get(i).getTitle());
                                                Log.i(TAG, "Add to adapter");
                                                mAdapter.add(element);
                                            }
                                            Log.i(TAG, "found " + didl.getItems().size() + " items.");
                                            for (int i = 0; i < didl.getItems().size(); i++) {
                                                Log.i(TAG, didl.getItems().get(i).getTitle() + ", id = " + didl.getItems().get(i).getId());
                                                mAdapter.add(new VideoElement(true, null, didl.getItems().get(i).getId(), didl.getItems().get(i).getTitle()));
                                            }
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            });*/
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
                                }
                            }
                        }
                    }
                });
            }
        }

        public void deviceRemoved(final Device device) {
            /*runOnUiThread(new Runnable() {
                public void run() {
                    listAdapter.remove(new DeviceDisplay(device));
                }
            });*/
        }
    }

    /*@Override
    public void onBackPressed() {
        if (mCurrent == null || mCurrent.getPath().equals(mRoot)) {
            super.onBackPressed();
        }
        else {
            parseAndUpdate(mCurrent.getParent());
        }
    }*/
}
