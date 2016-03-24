package com.freak.videosenfants;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
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

public class BrowseDlnaActivity extends BrowseActivity implements AdapterView.OnItemClickListener, DialogInterface.OnCancelListener, RetrieveDeviceThradListener{

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseDlnaActivity.class.getSimpleName();

    private ListView mListView;
    private Handler mHandler;
    private RemoteService mService;
    private BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();
    private AndroidUpnpService mUpnpService;
    private VideoElementAdapter mAdapter;
    private ProgressDialog mDialog;
    private int mIndex;

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
            mIndex = 0;
            findDevice(mIndex);
        }

        public void onServiceDisconnected(ComponentName className) {
            mUpnpService = null;
        }
    };

    private void findDevice(int index) {
        if (DEBUG)
            Log.i(TAG, "Trying to connect to DLNA server " + index);
        if (index < getResources().getInteger(R.integer.dlna_servers_number)){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BrowseDlnaActivity.this);
            String key = getString(R.string.key_dlna_browse) + "_" + index;
            if ((prefs.getBoolean(key + getString(R.string.key_visible), false)) &&
                (prefs.getString(key, "").length() > 0)) {
                if (DEBUG)
                    Log.i(TAG, "DLNA server " + index +" is defined (key " + key + "), trying to connect");

                mRoot = prefs.getString(key + getString(R.string.key_path), "0");
                String udn = prefs.getString(key + getString(R.string.key_udn), "");
                String url =  prefs.getString(key + getString(R.string.key_url), "");
                int maxAge = prefs.getInt(key + getString(R.string.key_max_age), 0);

                RetrieveDeviceThread thread = new RetrieveDeviceThread(mUpnpService, udn, url, maxAge, BrowseDlnaActivity.this);
                thread.start();
            }
            else {
                if (DEBUG)
                    Log.i(TAG, "DLNA server " + index +" is not defined, trying to connect to another one");
                mIndex++;
                findDevice(mIndex);
            }
        }
        else {
            if (DEBUG)
                Log.i(TAG, "No known device have been found");
        }
    }

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

        mDialog = ProgressDialog.show(this, getString(R.string.dlna_progress_dialog_title), getString(R.string.dlna_progress_dialog_text), true, true, this);
        mDialog.setCanceledOnTouchOutside(false);
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
                            didl.getContainers().get(i).getId(),
                            didl.getContainers().get(i).getTitle(),
                            mCurrent,
                            BrowseDlnaActivity.this);
                    mAdapter.add(element);
                }

                if (DEBUG)
                    Log.i(TAG, "found " + didl.getItems().size() + " items.");
                for (int i = 0; i < didl.getItems().size(); i++) {
                    mAdapter.add(new VideoElement(
                            false,
                            didl.getItems().get(i).getResources().get(0).getValue(),
                            didl.getItems().get(i).getTitle(),
                            mCurrent,
                            BrowseDlnaActivity.this));
                }
                mAdapter.notifyDataSetChanged();
                mListView.setSelectionAfterHeaderView();
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onBackPressed();
    }

    @Override
    public void onDeviceNotFound() {
        if (DEBUG)
            Log.i(TAG, "Unable to connect to DLNA server " + mIndex +", trying another one");
        mIndex++;
        findDevice(mIndex);
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
                                            mCurrent = new VideoElement(true, mRoot, "Root", null, BrowseDlnaActivity.this);
                                            mDialog.dismiss();
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
    }

}
