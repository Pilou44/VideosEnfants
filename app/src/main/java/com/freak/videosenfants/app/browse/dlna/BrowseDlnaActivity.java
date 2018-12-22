package com.freak.videosenfants.app.browse.dlna;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.browse.BrowseActivity;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.browsing.RetrieveDeviceThread;
import com.freak.videosenfants.elements.browsing.RetrieveDeviceThreadListener;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.freak.videosenfants.app.browse.VideoElementAdapter;

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
import org.fourthline.cling.support.model.Res;

import java.io.File;

public class BrowseDlnaActivity extends BrowseActivity implements AdapterView.OnItemClickListener, DialogInterface.OnCancelListener, RetrieveDeviceThreadListener,DialogInterface.OnClickListener{

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseDlnaActivity.class.getSimpleName();

    private ListView mListView;
    private Handler mHandler;
    private RemoteService mService;
    private final BrowseRegistryListener mRegistryListener = new BrowseRegistryListener();
    private AndroidUpnpService mUpnpService;
    private VideoElementAdapter mAdapter;
    private ProgressDialog mDialog;
    private int mIndex;
    private boolean mBound;

    // Used for copy
    private Spinner mDest;
    private String mSrc;
    private String mSelected;

    private boolean[] mTestedDlnas;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

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
            mTestedDlnas = new boolean[getResources().getInteger(R.integer.dlna_servers_number)];
            for (int i = 0 ; i < mTestedDlnas.length ; i++) {
                mTestedDlnas[i] = false;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BrowseDlnaActivity.this);
            mIndex = prefs.getInt(getString(R.string.key_last_used_dlna), 0);
            findDevice(mIndex);
        }

        public void onServiceDisconnected(ComponentName className) {
            mUpnpService = null;
        }
    };

    private void findDevice(int index) {
        if (DEBUG)
            Log.i(TAG, "Trying to connect to DLNA server " + index);
        if (!mTestedDlnas[mIndex]){
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
                mTestedDlnas[mIndex] = true;
                mIndex = (mIndex + 1) % getResources().getInteger(R.integer.dlna_servers_number);
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
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowseDlnaActivity.this.onBackPressed();
            }
        });

        getDialog().setContentView(R.layout.browse_dlna_context_menu_layout);

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );
        // Now you can enable logging as needed for various categories of Cling:
        // Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);

        mHandler = new Handler();
        mBound = false;

        mListView = (ListView) findViewById(R.id.listView);
        assert mListView != null;
        mListView.setOnItemClickListener(this);
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(this);

        mAdapter = new VideoElementAdapter(this);
        mAdapter.setNotifyOnChange(false);

        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG)
            Log.i(TAG, "Test WiFi connexion");

        if (ApplicationSingleton.getInstance().isWiFiConnected(this)) {
            if (mUpnpService == null || mService == null || mCurrent == null) {
                if (DEBUG)
                    Log.i(TAG, "Bind service");

                // This will start the UPnP service if it wasn't already started
                getApplicationContext().bindService(
                        new Intent(this, AndroidUpnpServiceImpl.class),
                        mServiceConnection,
                        BIND_AUTO_CREATE
                );
                mBound = true;

                mDialog = ProgressDialog.show(this, getString(R.string.dlna_progress_dialog_title), getString(R.string.progress_dialog_text), true, true, this);
                mDialog.setCanceledOnTouchOutside(false);
            } else {
                mUpnpService.getControlPoint().execute(new Browse(mService, mCurrent.getPath(), BrowseFlag.DIRECT_CHILDREN) {
                    @Override
                    public void received(ActionInvocation arg0,
                                         DIDLContent didl) {
                        parseAndUpdate(didl);
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
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_wifi_connexion);
            builder.setNeutralButton(R.string.ok, this);
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpnpService != null) {
            mUpnpService.getRegistry().removeListener(mRegistryListener);
        }
        // This will stop the UPnP service if nobody else is bound to it
        if (mBound) {
            getApplicationContext().unbindService(mServiceConnection);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final VideoElement element = (VideoElement) parent.getItemAtPosition(position);

        if (element.isDirectory()) {
            if (DEBUG)
                Log.i(TAG, "Directory clicked");
            parseAndUpdate(element);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.getPath()));
            intent.setDataAndType(Uri.parse(element.getPath()), "video/*");
            startActivity(intent);
        }
    }

    protected void parseAndUpdate(final VideoElement element) {

        mDialog = ProgressDialog.show(this, getString(R.string.dlna_progress_dialog_files_title), getString(R.string.progress_dialog_text), true, true, this);
        mDialog.setCanceledOnTouchOutside(false);

        mUpnpService.getControlPoint().execute(new Browse(mService, element.getPath(), BrowseFlag.DIRECT_CHILDREN) {
            @Override
            public void received(ActionInvocation arg0,
                                 DIDLContent didl) {
                parseAndUpdate(didl);
                goToTop();
                mDialog.dismiss();
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
                            mCurrent);
                    element.setPathFromRoot(mCurrent.getPathFromRoot() + "/" + element.getName());
                    mAdapter.add(element);
                }

                if (DEBUG)
                    Log.i(TAG, "found " + didl.getItems().size() + " items.");
                for (int i = 0; i < didl.getItems().size(); i++) {
                    VideoElement element = new VideoElement(
                            false,
                            didl.getItems().get(i).getResources().get(0).getValue(),
                            didl.getItems().get(i).getTitle(),
                            mCurrent,
                            mListView);
                    element.setPathFromRoot(mCurrent.getPathFromRoot() + "/" + element.getName());
                    for (final Res resource : didl.getItems().get(i).getResources()) {
                        if (resource.getSize() != null)
                            element.setSize(resource.getSize());
                    }
                    mAdapter.add(element);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void goToTop() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
        mTestedDlnas[mIndex] = true;
        mIndex = (mIndex + 1) % getResources().getInteger(R.integer.dlna_servers_number);
        findDevice(mIndex);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            dialog.dismiss();
            this.finish();
        }
    }

    @Override
    protected void prepareContextMenu(final AdapterView<?> parent, final int position) {
        super.prepareContextMenu(parent, position);

        final VideoElement element = (VideoElement) parent.getItemAtPosition(position);

        Button copyButton = (Button) getDialog().findViewById(R.id.copy_button);

        if (element.isDirectory()){
            copyButton.setVisibility(View.GONE);
        }
        else {
            copyButton.setVisibility(View.VISIBLE);
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(BrowseDlnaActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(BrowseDlnaActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                ApplicationSingleton.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(BrowseDlnaActivity.this);
                    builder.setTitle(R.string.copy);
                    builder.setView(R.layout.copy_dialog_layout);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCopy();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    mSrc = element.getPath();
                    mSelected = element.getName();
                    TextView src = (TextView) dialog.findViewById(R.id.copy_src);
                    String text = mSelected + " (" + ApplicationSingleton.getInstance().formatByteSize(BrowseDlnaActivity.this, element.getSize()) + ")";
                    assert src != null;
                    src.setText(text);

                    // ToDo refactor
                    /*mDest = (Spinner) dialog.findViewById(R.id.copy_dest);
                    DestSpinnerAdapter adapter = new DestSpinnerAdapter(BrowseDlnaActivity.this, BrowseLocalActivity.getRoots(BrowseDlnaActivity.this));
                    mDest.setAdapter(adapter);
                    mDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (element.getSize() > ((File)mDest.getSelectedItem()).getFreeSpace()){
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                            else {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });*/

                    getDialog().dismiss();
                }
            });
        }
    }

    private void startCopy() {
        final String source = mSrc;
        String parentPath = mDest.getSelectedItem() + mCurrent.getPathFromRoot();
        final String dest = parentPath + "/" + mSelected + mSrc.substring(mSrc.lastIndexOf("."));

        if (DEBUG)
            Log.i(TAG, "Start copy from " + source + " to " + dest);

        File parentFile = new File(parentPath);
        boolean dirExists = parentFile.exists();
        if (!dirExists) {
            dirExists = parentFile.mkdirs();
            if (DEBUG)
                Log.i(TAG, "Directory created: " + dirExists);
        }

        if (!dirExists) {
            Log.e(TAG, "Can't access destination directory");
        }
        else {
            final File destFile = new File(dest);

            if (destFile.exists()) {
                if (DEBUG)
                    Log.i(TAG, "Destination file already exists");
                AlertDialog.Builder existDestDialog = new AlertDialog.Builder(this);
                existDestDialog.setTitle(R.string.exist_dest_dialog_title);
                existDestDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DEBUG)
                            Log.i(TAG, "Override");
                        dialog.dismiss();
                        if (destFile.delete()) {
                            launchCopy(source, destFile);
                        }
                    }
                });
                existDestDialog.setNeutralButton(R.string.rename, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DEBUG)
                            Log.i(TAG, "Rename");
                        dialog.dismiss();
                        launchCopy(source, destFile);
                    }
                });
                existDestDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DEBUG)
                            Log.i(TAG, "Cancel copy");
                        dialog.dismiss();
                    }
                });
                existDestDialog.create().show();
            }
            else {
                launchCopy(source, destFile);
            }
        }
    }

    private void launchCopy(String source, File destination) {
        if (DEBUG)
            Log.i(TAG, "Create download request");

        Uri srcUri = Uri.parse(source);
        Uri destUri = Uri.fromFile(destination);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(srcUri);
        request.setDestinationUri(destUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(destination.getName());
        request.setVisibleInDownloadsUi(true);

        if (DEBUG)
            Log.i(TAG, "Send download request");

        downloadManager.enqueue(request);
    }

    class BrowseRegistryListener extends DefaultRegistryListener {

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
                                            mCurrent = new VideoElement(true, mRoot, "Root", null);
                                            mCurrent.setPathFromRoot("");
                                            parseAndUpdate(didl);
                                            goToTop();
                                            mDialog.dismiss();

                                            if (DEBUG)
                                                Log.i(TAG, "Store last used DLNA: " + mIndex);
                                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(BrowseDlnaActivity.this).edit();
                                            edit.putInt(getString(R.string.key_last_used_dlna), mIndex);
                                            edit.apply();
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
