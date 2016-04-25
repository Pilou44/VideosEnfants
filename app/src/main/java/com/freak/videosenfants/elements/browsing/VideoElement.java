package com.freak.videosenfants.elements.browsing;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.services.GetThumbnailsService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class VideoElement {

    private static final boolean DEBUG = true;
    private static final String TAG = VideoElement.class.getSimpleName();

    private final Context mContext;
    private final boolean mDirectory;
    private final String mPath;
    private String mName;
    private final VideoElement mParent;
    private Long mSize;
    private String mPathFromRoot;
    private GetThumbnailsService mService;
    private boolean mBound;
    private DisplayImageOptions mOptions;
    private ImageView mView;
    private ImageLoader mImageLoader;
    private Handler mHandler;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) {
                Log.i(TAG, "Service disconnected");
            }

            mBound = false;
            mService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) {
                Log.i(TAG, "Service connected");
            }

            mBound = true;
            GetThumbnailsService.LocalBinder mLocalBinder = (GetThumbnailsService.LocalBinder) service;
            mService = mLocalBinder.getService();
            mService.enqueue(VideoElement.this);
        }
    };

    public VideoElement(boolean directory, String path, String name, VideoElement parent, Context context) {
        mContext = context;
        mDirectory = directory;
        mPath = path;
        mName = name;
        mParent = parent;
    }

    public VideoElement(File file, VideoElement parent, Context context){
        mContext = context;
        mDirectory = file.isDirectory();
        mPath = file.getAbsolutePath();
        mName = file.getName();
        if (!mDirectory){
            mName = mName.substring(0, mName.lastIndexOf("."));
        }
        mParent = parent;
        mSize = file.length();
    }

    public boolean isDirectory() {
        return mDirectory;
    }

    public String getPath() {
        return mPath;
    }

    public String getName() {
        return mName;
    }
    
    public VideoElement getParent() {
        return mParent;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getSize() {
        return mSize;
    }

    public void setPathFromRoot(String pathFromRoot) {
        mPathFromRoot = pathFromRoot;
    }

    public String getPathFromRoot(){
        return mPathFromRoot;
    }

    public String getImageURI() {
        String uri = getBitmapURI();
        if (uri == null && !mDirectory) {
            if (DEBUG) {
                Log.i(TAG, "Try to get cached image");
            }
            uri = getCachedBitmapURI();
            if (uri == null) {
                if (DEBUG) {
                    Log.i(TAG, "No cached image, bind service");
                }
                Intent mIntent = new Intent(mContext, GetThumbnailsService.class);
                mContext.bindService(mIntent, mConnection, Service.BIND_AUTO_CREATE);
            }
        }
        return uri;
    }

    public String getBitmapURI() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        File picturesLocation = new File(sharedPref.getString("local_pictures", mContext.getString(R.string.default_local_pictures)));
        boolean locationExists = picturesLocation.exists();
        if (!locationExists) {
            if (DEBUG) {
                Log.i(TAG, "Create directory: " + picturesLocation.getAbsolutePath());
            }
            locationExists = picturesLocation.mkdir();
            if (DEBUG) {
                Log.i(TAG, "Directory created: " + locationExists);
            }
        }

        if (locationExists) {
            if (DEBUG) {
                Log.i(TAG, "Pictures location: " + picturesLocation.getAbsolutePath());
            }

            String fileNamePng = mName + ".png";
            String fileNameJpg = mName + ".jpg";
            String fileNameJpeg = mName + ".jpeg";

            File picPng = new File(picturesLocation, fileNamePng);
            File picJpg = new File(picturesLocation, fileNameJpg);
            File picJpeg = new File(picturesLocation, fileNameJpeg);

            if (picPng.exists()) {
                return "file://" + picPng.getAbsolutePath();
            } else if (picJpg.exists()) {
                return "file://" + picJpg.getAbsolutePath();
            } else if (picJpeg.exists()) {
                return "file://" + picJpeg.getAbsolutePath();
            }
        }
        return null;
    }

    public String getCachedBitmapURI() {
        File imageFile = new File(mContext.getExternalCacheDir(), mName + ".jpg");
        if (imageFile.exists()) {
            return "file://" + imageFile.getAbsolutePath();
        }
        else {
            return null;
        }
    }

    public void unbind() {
        if(mBound) {
            if (DEBUG) {
                Log.i(TAG, "Unbind service");
            }

            try {
                mContext.unbindService(mConnection);
            }
            catch (IllegalArgumentException e){
                Log.w(TAG, "Service not bound");
            }
            mBound = false;
        }
    }

    public void update() {
        if (mView.getTag().equals(this)) {
            if (DEBUG)
                Log.i(TAG, "Update image");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mImageLoader.displayImage(getImageURI(), mView, mOptions);
                }
            });
        }
    }

    public void setView(ImageView imageView, Handler handler, ImageLoader imageLoader, DisplayImageOptions options) {
        mHandler = handler;
        mOptions = options;
        mImageLoader = imageLoader;
        mView = imageView;
        mView.setTag(this);
    }
}
