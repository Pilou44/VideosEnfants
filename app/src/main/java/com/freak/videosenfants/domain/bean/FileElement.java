package com.freak.videosenfants.domain.bean;

import java.io.File;

public class FileElement {

    private String mName;
    private final File mFile;
    private boolean mExpanded;
    private int mIndent = 0;

    @SuppressWarnings("WeakerAccess")
    public FileElement(File file) {
        mFile = file;
        mExpanded = false;
        mName = file.getName();
    }

    public FileElement(File file, int indent) {
        this(file);
        mIndent = indent;
    }

    public FileElement(File file, String name) {
        this(file);
        mName = name;
    }

    public void setExpanded(@SuppressWarnings("SameParameterValue") boolean expanded) {
        mExpanded = expanded;
    }

    public File getFile() {
        return mFile;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public String getName() {
        return mName;
    }

    public int getIndent() {
        return mIndent;
    }
}
