package com.freak.videosenfants.domain.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

@Entity(tableName = "local_roots")
public class VideoElement implements BaseElement {

    private static final String TAG = VideoElement.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long mId;

    //private final Context mContext;
    @ColumnInfo(name = "path")
    public final String mPath;

    //private ListView mListView;
    @Ignore
    private final boolean mDirectory;
    @Ignore
    private String mName;
    @Ignore
    private VideoElement mParent;
    @Ignore
    private Long mSize;
    @Ignore
    private String mPathFromRoot;
    //private GetThumbnailsService mService;
    //private boolean mBound;
    //private DisplayImageOptions mOptions;
    //private ImageLoader mImageLoader;
    //private Handler mHandler;
    //private int mPosition;

    /*private final ServiceConnection mConnection = new ServiceConnection() {

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
    };*/

    public VideoElement(long id, String path) {
        mId = id;
        mPath = path;
        mName = path;
        mDirectory = true;
    }

    public VideoElement(boolean directory, String path, String name, VideoElement parent) {
        mDirectory = directory;
        mPath = path;
        mName = name;
        mParent = parent;
    }

    public VideoElement(File file, VideoElement parent){
        mDirectory = file.isDirectory();
        mPath = file.getAbsolutePath();
        mName = file.getName();
        if (!mDirectory){
            mName = mName.substring(0, mName.lastIndexOf("."));
        }
        mParent = parent;
        mSize = file.length();
    }

    public VideoElement(File file, VideoElement parent, ListView listView) {
        this(file, parent);
        //mListView = listView;
    }

    public VideoElement(@SuppressWarnings("SameParameterValue") boolean directory, String path, String name, VideoElement parent, ListView listView) {
        this(directory, path, name, parent);
        //mListView = listView;
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

    /*public String getImageURI() {
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
    }*/

    /*private String getBitmapURI() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        File picturesLocation = new File(sharedPref.getString(mContext.getString(R.string.key_local_pictures), mContext.getString(R.string.default_local_pictures)));
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
    }*/

    /*private String getCachedBitmapURI() {
        File imageFile = new File(mContext.getExternalCacheDir(), mName + ".jpg");
        if (imageFile.exists()) {
            return "file://" + imageFile.getAbsolutePath();
        }
        else {
            return null;
        }
    }*/

    /*public void unbind() {
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
    }*/

    /*public void update(final boolean thumbnailExtracted) {
        if (DEBUG) {
            Log.i(TAG, "Update " + mName);
            //Log.i(TAG, "Current element is " + ((VideoElement)mView.getTag()).getName());
        }
        if (mListView != null) {
            if (DEBUG)
                Log.i(TAG, "list view not null");

            int start = mListView.getFirstVisiblePosition();
            int stop = mListView.getLastVisiblePosition();

            if (DEBUG)
                Log.i(TAG, "Position: " + mPosition + ", start: " + start + ", stop: " + stop);

            if (mPosition >= start && mPosition <= stop) {
                if (DEBUG)
                    Log.i(TAG, mName + " is visible");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        View v = mListView.getChildAt(mPosition - mListView.getFirstVisiblePosition());

                        if(v == null)
                            return;

                        ImageView view = (ImageView) v.findViewById(R.id.icon);

                        if (thumbnailExtracted) {
                            mImageLoader.displayImage(getImageURI(), view, mOptions);
                        }
                        else {
                            mImageLoader.displayImage("drawable://" + R.drawable.fichier, view, mOptions);
                        }
                    }
                });
            }
        }
    }*/

    public void setPosition(int position, Handler handler, ImageLoader imageLoader, DisplayImageOptions options) {
        //if (DEBUG)
        //Log.i(TAG, "Set position for " + mName + ": " + position);
        //mPosition = position;
        //mHandler = handler;
        //mOptions = options;
        //mImageLoader = imageLoader;
    }

    public void setParent(VideoElement parent) {
        mParent = parent;
    }
}
