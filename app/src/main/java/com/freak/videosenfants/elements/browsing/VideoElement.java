package com.freak.videosenfants.elements.browsing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.preference.PreferenceManager;
import android.util.Log;

import com.freak.videosenfants.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import wseemann.media.FFmpegMediaMetadataRetriever;

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
        if (uri != null) {
            return uri;
        }
        else if (!mDirectory) {
            return getThumbnail();        }
        else {
            return null;
        }
    }

    private String getThumbnail() {
        File imageFile = new File(mContext.getExternalCacheDir(), mName + ".png");

        if (DEBUG)
            Log.i(TAG, "Thumbnail's path: " + imageFile.getAbsolutePath());

        if (imageFile.exists()) {
            if (DEBUG)
                Log.i(TAG, "Thumbnail exists in cache");
            return "file://" + imageFile.getAbsolutePath();
        }
        else {
            if (DEBUG) {
                Log.i(TAG, "Extract new thumbnail");
            }

            Bitmap bmp = null;
            try {
                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                mmr.setDataSource(mPath);
                bmp = mmr.getFrameAtTime(120000000); // frame at 120 seconds
                mmr.release();
            }
            catch (IllegalArgumentException e) {
                Log.e(TAG, "Error ith source (" + mPath + ")");
                e.printStackTrace();
            }

            if (bmp == null) {
                if (DEBUG) {
                    Log.i(TAG, "Error extracting thumbnail");
                }
                return null;
            } else {
                int pxWidth = mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_width);
                int pxHeight = mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_height);
                bmp = ThumbnailUtils.extractThumbnail(bmp, pxWidth, pxHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imageFile);
                    bmp.compress(Bitmap.CompressFormat.PNG, 80, fos);
                    fos.close();
                    bmp.recycle();
                    if (DEBUG) {
                        Log.i(TAG, "Thumbnail extracted");
                    }
                    return "file://" + imageFile.getAbsolutePath();
                }
                catch (IOException e) {
                    if (DEBUG) {
                        Log.i(TAG, "Error writing thumbnail");
                    }
                    Log.e(TAG, e.getMessage());
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    return null;
                }
            }


        }

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
}
