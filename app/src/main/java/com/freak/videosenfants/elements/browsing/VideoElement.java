package com.freak.videosenfants.elements.browsing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.freak.videosenfants.R;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoElement {

    private static final boolean DEBUG = true;
    private static final String TAG = VideoElement.class.getSimpleName();

    private final Context mContext;
    private final boolean mDirectory;
    private Drawable mIcon;
    private final String mPath;
    private String mName;
    private final VideoElement mParent;
    private Long mSize;
    private String mPathFromRoot;

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

    public Drawable getIcon() {
        return mIcon;
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

    private Drawable getDrawableForElement() {
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
                Bitmap bMap = BitmapFactory.decodeFile(picPng.getAbsolutePath());
                return new BitmapDrawable(mContext.getResources(), bMap);
            } else if (picJpg.exists()) {
                Bitmap bMap = BitmapFactory.decodeFile(picJpg.getAbsolutePath());
                return new BitmapDrawable(mContext.getResources(), bMap);
            } else if (picJpeg.exists()) {
                Bitmap bMap = BitmapFactory.decodeFile(picJpeg.getAbsolutePath());
                return new BitmapDrawable(mContext.getResources(), bMap);
            }
        }
        return null;
    }

    void generateScreenshot() {
        Drawable ret = getDrawableForElement();
        if (ret != null) {
            mIcon = ret;
        }
        else {
            if (DEBUG) {
                Log.i(TAG, "No local image found");
            }
            if (mDirectory) {
                mIcon = mContext.getDrawable(R.drawable.empty);
            } else {
                if (DEBUG) {
                    Log.i(TAG, "Try to extract thumbnail");
                }

                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                mmr.setDataSource(mPath);
                Bitmap bmp = mmr.getFrameAtTime(120000000); // frame at 120 seconds
                mmr.release();

                if (bmp == null) {
                    mIcon = mContext.getResources().getDrawable(R.drawable.fichier, null);
                }
                else {
                    mIcon = new BitmapDrawable(mContext.getResources(), bmp);
                }

                if (DEBUG) {
                    Log.i(TAG, "Thumbnail extraction finished");
                }
            }
        }
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
}
