package com.freak.videosenfants;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class VideoElement {

    private static final boolean DEBUG = true;
    protected static final String TAG = VideoElement.class.getSimpleName();

    private Context mContext;
    private boolean mDirectory;
    private Drawable mIcon;
    private String mPath;
    private String mName;
    private VideoElement mParent;

    public VideoElement(boolean directory, String path, String name, VideoElement parent, Context context) {
        mContext = context;
        mDirectory = directory;
        mPath = path;
        mName = name;
        mParent = parent;
        mIcon = generateScreenshot();
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
        mIcon = generateScreenshot();
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
            locationExists = picturesLocation.mkdir();
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

    protected Drawable generateScreenshot() {
        Drawable ret = getDrawableForElement();
        if (ret == null) {
            if (mDirectory) {
                return null;
            } else {
                Bitmap bmp = ThumbnailUtils.createVideoThumbnail(mPath, MediaStore.Images.Thumbnails.MINI_KIND);
                if (bmp == null) {
                    ret = mContext.getResources().getDrawable(R.drawable.fichier, null);
                }
                else {
                    ret = new BitmapDrawable(mContext.getResources(), bmp);
                }
                return ret;
            }
        }
        else {
            return ret;
        }
    }

}
