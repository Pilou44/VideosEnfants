package com.freak.videosenfants.domain.bean;

public abstract class BrowsableElement {
    private boolean mExpanded;
    private int mIndent;

    public BrowsableElement(int indent) {
        mExpanded = false;
        mIndent = indent;
    }

    public void setExpanded(@SuppressWarnings("SameParameterValue") boolean expanded) {
        mExpanded = expanded;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public int getIndent() {
        return mIndent;
    }

    public abstract String getName();

    public void setIndent(int indent) {
        mIndent = indent;
    }
}
