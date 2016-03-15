package com.freak.videosenfants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

public abstract class BrowseActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    protected static final String TAG = BrowseActivity.class.getSimpleName();
    private static final String PICTURES_LOCATION = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/VideosForChilds/";
    protected VideoElement mCurrent;
    protected String mRoot = "root";

    private Drawable getDrawableForElement(String file, boolean isDirectory) {
        File picturesLocation = new File(PICTURES_LOCATION);
        if (!picturesLocation.exists()) {
            picturesLocation.mkdir();
        }
        if (DEBUG) {
            Log.i(TAG, "Pictures location: " + picturesLocation.getAbsolutePath());
        }

        String fileNamePng = file;
        String fileNameJpg = file;
        String fileNameJpeg = file;
        if (isDirectory) {
            fileNamePng += ".png";
            fileNameJpg += ".jpg";
            fileNameJpeg += ".jpeg";
        }
        else {
            fileNamePng = fileNamePng + ".png";
            fileNameJpg = fileNameJpg + ".jpg";
            fileNameJpeg = fileNameJpeg + ".jpeg";
        }
        File picPng = new File(picturesLocation, fileNamePng);
        File picJpg = new File(picturesLocation, fileNameJpg);
        File picJpeg = new File(picturesLocation, fileNameJpeg);

        if (picPng.exists()) {
            Bitmap bMap = BitmapFactory.decodeFile(picPng.getAbsolutePath());
            return new BitmapDrawable(this.getResources(), bMap);
        }
        else if (picJpg.exists()) {
            Bitmap bMap = BitmapFactory.decodeFile(picJpg.getAbsolutePath());
            return new BitmapDrawable(this.getResources(), bMap);
        }
        else if (picJpeg.exists()) {
            Bitmap bMap = BitmapFactory.decodeFile(picJpeg.getAbsolutePath());
            return new BitmapDrawable(this.getResources(), bMap);
        }
        else {
            return null;
        }
    }

    protected Drawable generateScreenshot(String path, String name, boolean isDirectory) {
        Drawable ret = getDrawableForElement(name, isDirectory);
        if (ret == null) {
            if (isDirectory) {
                return this.getResources().getDrawable(R.drawable.dossier, null);
            } else {
                Bitmap bmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                if (bmp == null) {
                    ret = this.getResources().getDrawable(R.drawable.fichier, null);
                }
                else {
                    ret = new BitmapDrawable(this.getResources(), bmp);
                }
                return ret;
            }
        }
        else {
            return ret;
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrent == null || mCurrent.getPath().equals(mRoot)) {
            super.onBackPressed();
        }
        else {
            parseAndUpdate(mCurrent.getParent());
        }
    }

    protected abstract void parseAndUpdate(VideoElement parent);

}
