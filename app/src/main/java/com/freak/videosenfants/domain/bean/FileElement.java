package com.freak.videosenfants.domain.bean;

import java.io.File;

public class FileElement extends BrowsableElement {

    private String mName;
    private final File mFile;

    @SuppressWarnings("WeakerAccess")
    public FileElement(File file) {
        this(file, 0);
    }

    public FileElement(File file, int indent) {
        super(indent);
        mFile = file;
        mName = file.getName();
    }

    public FileElement(File file, String name) {
        this(file);
        mName = name;
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public String getName() {
        return mName;
    }
}
