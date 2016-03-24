package com.freak.videosenfants.elements.preferences;

import android.util.Log;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.RemoteService;

public class DlnaElement {
    private static final String TAG = DlnaElement.class.getSimpleName();
    private static final boolean DEBUG = true;
    private RemoteService mService;
    private String mName;
    private String mUdn;
    private String mUrl;
    private String mPath;
    private int mMaxAge;
    private boolean mExpanded;
    private int mIndent = 0;

    public DlnaElement(String udn, String url, String path, int maxAge, String name, int indent) {
        mUdn = udn;
        mUrl = url;
        mPath = path;
        mMaxAge = maxAge;
        mName = name;
        mIndent = indent;
        mExpanded = false;
    }

    public DlnaElement(Device device, RemoteService service) {
        mUdn = device.getIdentity().getUdn().getIdentifierString();
        mUrl = ((RemoteDeviceIdentity)device.getIdentity()).getDescriptorURL().toString();
        mMaxAge = device.getIdentity().getMaxAgeSeconds();
        mService = service;
        mPath = "0";
        mName = device.getDisplayString();
        mExpanded = false;
        if (DEBUG)
         Log.i(TAG, "New device " + mName);
    }

    public DlnaElement(String title, String path, DlnaElement parent) {
        mUdn = parent.getUdn();
        mUrl = parent.getUrl();
        mMaxAge = parent.getMaxAge();
        mService = parent.getService();
        mPath = path;
        mName = title;
        mExpanded = false;
        mIndent = parent.getIndent() + 1;
    }

    public String getName() {
        return mName;
    }

    public int getIndent() {
        return mIndent;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    public String getPath() {
        return mPath;
    }

    public RemoteService getService() {
        return mService;
    }

    public String getUdn() {
        return mUdn;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getMaxAge() {
        return mMaxAge;
    }
}
