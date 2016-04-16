package com.freak.videosenfants.elements.imagesearch;

import android.graphics.Bitmap;

public class SingleImage {
    private final String mName;
    private final Bitmap mImage1;

    public SingleImage(Bitmap image1, String name){
        mImage1 = image1;
        mName = name;
    }

    public Bitmap getImage1() {
        return mImage1;
    }

    public String getName() {
        return mName;
    }
}
