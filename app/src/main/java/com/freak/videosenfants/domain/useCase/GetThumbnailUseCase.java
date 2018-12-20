package com.freak.videosenfants.domain.useCase;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;

import com.freak.videosenfants.R;
import com.freak.videosenfants.dagger.scope.PerActivity;
import com.freak.videosenfants.domain.bean.VideoElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import wseemann.media.FFmpegMediaMetadataRetriever;

@PerActivity
public class GetThumbnailUseCase extends UseCase<Uri, VideoElement> {

    private final Context mContext;

    @Inject
    GetThumbnailUseCase(Scheduler postExecutionThread, Context context) {
        super(postExecutionThread);
        mContext = context;
    }

    @Override
    protected Observable<Uri> buildObservable(VideoElement element) {
        return Observable.defer(() -> {
            Uri uri = getCachedBitmapURI(element);
            if (uri == null) {
                if (element.isDirectory()) {
                    return Observable.error(new Exception());
                } else {
                    return createThumbnail(element);
                }
            } else {
                return Observable.just(uri);
            }
        });

    }

    private Observable<Uri> createThumbnail(VideoElement element) {
        File imageFile = new File(mContext.getExternalCacheDir(), element.getName() + ".jpg");
        Bitmap bmp;
        try {
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(element.getPath());
            bmp = mmr.getFrameAtTime(120000000); // frame at 120 seconds
            mmr.release();
        }
        catch (IllegalArgumentException e) {
            return Observable.error(e);
        }

        int pxWidth = mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_width);
        int pxHeight = mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_height);
        bmp = ThumbnailUtils.extractThumbnail(bmp, pxWidth, pxHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, fos);
            fos.close();
            bmp.recycle();
            return Observable.just(Uri.fromFile(imageFile));
        }
        catch (IOException e) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return Observable.error(e);
        }
    }

    private Uri getCachedBitmapURI(VideoElement element) {
        File imageFile = new File(mContext.getExternalCacheDir(), element.getName() + ".jpg");
        if (imageFile.exists()) {
            return Uri.fromFile(imageFile);
        } else {
            return null;
        }
    }
}
