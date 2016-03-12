package com.freak.videosenfants;

import android.graphics.drawable.Drawable;

import java.io.File;

public class VideoElement {

    private boolean mDirectory;
    private Drawable mIcon;
    private String mPath;
    private String mName;
    private VideoElement mParent;

    public VideoElement(boolean directory, Drawable icon, String path, String name, VideoElement parent) {
        mDirectory = directory;
        mIcon = icon;
        mPath = path;
        mName = name;
        mParent = parent;
    }

    public VideoElement(File file, Drawable icon, VideoElement parent){
        mDirectory = file.isDirectory();
        mIcon = icon;
        mPath = file.getAbsolutePath();
        mName = file.getName();
        if (!mDirectory){
            mName = mName.substring(0, mName.lastIndexOf("."));
        }
        mParent = parent;
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

    public void setDirectory(boolean directory) {
        this.mDirectory = directory;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setParent(VideoElement parent) {
        this.mParent = mParent;
    }

    public VideoElement getParent() {
        return mParent;
    }
}
