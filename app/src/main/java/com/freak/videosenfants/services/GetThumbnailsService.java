package com.freak.videosenfants.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.browsing.VideoElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class GetThumbnailsService extends Service {
    private static final boolean DEBUG = true;
    private static final String TAG = GetThumbnailsThread.class.getSimpleName();

    private IBinder mBinder = new LocalBinder();

    private Vector<VideoElement> mQueue;
    private boolean mRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = new Vector<>();
        GetThumbnailsThread mThread = new GetThumbnailsThread();
        mRunning = true;
        mThread.start();
    }

    @Override
    public void onDestroy() {
        mRunning = false;
        super.onDestroy();
    }

    public void enqueue(VideoElement element) {
        if (DEBUG)
            Log.i(TAG, "Enqueue " + element.getName());
        mQueue.add(element);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public GetThumbnailsService getService() {
            return GetThumbnailsService.this;
        }
    }
    private class GetThumbnailsThread extends Thread {

        public void run() {
            while (mRunning) {
                if (mQueue.size() > 0){
                    VideoElement element = mQueue.get(0);

                    if (DEBUG)
                        Log.i(TAG, "Get thumbnail for " + element.getName());

                    File imageFile = new File(GetThumbnailsService.this.getExternalCacheDir(), element.getName() + ".jpg");

                    if (DEBUG)
                        Log.i(TAG, "Thumbnail's path: " + imageFile.getAbsolutePath());

                    if (imageFile.exists()) {
                        if (DEBUG)
                            Log.i(TAG, "Thumbnail exists in cache");
                        finishElement(element);
                    }
                    else if (   element.getPath().startsWith("http") &&
                                !ApplicationSingleton.getInstance(GetThumbnailsService.this).isWiFiConnected()) {
                        if (DEBUG) {
                            Log.i(TAG, "Element is DLNA but WiFi is disconnected");
                        }
                        finishElement(element);
                    }
                    else {
                        if (DEBUG) {
                            Log.i(TAG, "Extract new thumbnail");
                        }

                        Bitmap bmp = null;
                        try {
                            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                            mmr.setDataSource(element.getPath());
                            bmp = mmr.getFrameAtTime(120000000); // frame at 120 seconds
                            mmr.release();
                        }
                        catch (IllegalArgumentException e) {
                            Log.e(TAG, "Error with source (" + element.getPath() + ")");
                            finishElement(element);
                            e.printStackTrace();
                        }

                        if (bmp == null) {
                            if (DEBUG) {
                                Log.i(TAG, "Error extracting thumbnail");
                            }
                        } else {
                            int pxWidth = GetThumbnailsService.this.getResources().getDimensionPixelSize(R.dimen.thumbnail_width) / 2;
                            int pxHeight = GetThumbnailsService.this.getResources().getDimensionPixelSize(R.dimen.thumbnail_height) / 2;
                            bmp = ThumbnailUtils.extractThumbnail(bmp, pxWidth, pxHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(imageFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                                fos.close();
                                bmp.recycle();
                                if (DEBUG) {
                                    Log.i(TAG, "Thumbnail extracted");
                                }
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
                            }
                        }
                        finishElement(element);
                    }
                }
                else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void finishElement(VideoElement element) {
            if (DEBUG)
                Log.i(TAG, "Done for " + element.getName());

            element.unbind();
            mQueue.removeElement(element);
        }
    }
}
