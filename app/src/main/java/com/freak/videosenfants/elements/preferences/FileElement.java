package com.freak.videosenfants.elements.preferences;

import java.io.File;

public class FileElement {

    private final String mName;
    private File mFile;
    private boolean mExpanded;
    private int mIndent = 0;

    public FileElement(File file) {
        mFile = file;
        mExpanded = false;
        mName = file.getName();
    }

    public FileElement(File file, int indent) {
        mFile = file;
        mExpanded = false;
        mName = file.getName();
        mIndent = indent;
    }

    public FileElement(File file, String name) {
        mFile = file;
        mExpanded = false;
        mName = name;
    }

    public void setExpanded(boolean expanded) {
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
