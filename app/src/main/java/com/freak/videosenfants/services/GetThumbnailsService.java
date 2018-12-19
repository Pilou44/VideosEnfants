package com.freak.videosenfants.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.domain.bean.VideoElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class GetThumbnailsService extends Service {
    private static final boolean DEBUG = true;
    private static final String TAG = GetThumbnailsThread.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

    private Vector<VideoElement> mQueue;
    private boolean mRunning;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationId;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = new Vector<>();
        GetThumbnailsThread mThread = new GetThumbnailsThread();
        mRunning = true;

        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationId = 1;
        mBuilder = new NotificationCompat.Builder(GetThumbnailsService.this)
                .setSmallIcon(R.drawable.ic_notif_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(mQueue.size() + " " + getString(R.string.thumbnails_to_retrieve));

        mThread.start();
    }

    @Override
    public void onDestroy() {
        mRunning = false;
        mNotificationManager.cancel(mNotificationId);
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
                    mBuilder.setContentText(mQueue.size() + " " + getString(R.string.thumbnails_to_retrieve));
                    mNotificationManager.notify(
                            mNotificationId,
                            mBuilder.build());

                    VideoElement element = mQueue.get(0);

                    if (DEBUG)
                        Log.i(TAG, "Get thumbnail for " + element.getName());

                    File imageFile = new File(GetThumbnailsService.this.getExternalCacheDir(), element.getName() + ".jpg");

                    if (DEBUG)
                        Log.i(TAG, "Thumbnail's path: " + imageFile.getAbsolutePath());

                    if (imageFile.exists()) {
                        if (DEBUG)
                            Log.i(TAG, "Thumbnail exists in cache");
                        finishElement(element, true);
                    }
                    else if (   element.getPath().startsWith("http") &&
                                !ApplicationSingleton.getInstance().isWiFiConnected(GetThumbnailsService.this)) {
                        if (DEBUG) {
                            Log.i(TAG, "Element is DLNA but WiFi is disconnected");
                        }
                        finishElement(element, false);
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
                            e.printStackTrace();
                            finishElement(element, false);
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
                                    Log.i(TAG, "Thumbnail extracted for " + element.getName() + ", update view");
                                }
                                finishElement(element, true);
                            }
                            catch (IOException e) {
                                Log.w(TAG, "Error writing thumbnail");
                                Log.w(TAG, e.getMessage());
                                e.printStackTrace();
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                finishElement(element, false);
                            }
                        }
                    }
                }
                else {
                    mNotificationManager.cancel(mNotificationId);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void finishElement(VideoElement element, boolean thumbnailRetrieved) {
            if (DEBUG)
                Log.i(TAG, "Done for " + element.getName());

            element.update(thumbnailRetrieved);
            element.unbind();
            mQueue.removeElement(element);
        }
    }
}
