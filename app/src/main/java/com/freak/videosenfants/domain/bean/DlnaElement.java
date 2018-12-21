package com.freak.videosenfants.domain.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.RemoteService;

@Entity(tableName = "dlna_roots")
public class DlnaElement {

    private static final String TAG = DlnaElement.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long mId;

    @ColumnInfo(name = "udn")
    public String mUdn;
    @ColumnInfo(name = "url")
    public String mUrl;
    @ColumnInfo(name = "path")
    public String mPath;
    @ColumnInfo(name = "max_age")
    public int mMaxAge;

    @Ignore
    private RemoteService mService;
    @Ignore
    private String mName;
    @Ignore
    private boolean mExpanded;
    @Ignore
    private int mIndent = 0;

    public DlnaElement(long id, String udn, String url, String path, int maxAge) {
        mId = id;
        mUdn = udn;
        mUrl = url;
        mPath = path;
        mMaxAge = maxAge;
    }

    @SuppressWarnings("WeakerAccess")
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

    public void setExpanded(@SuppressWarnings("SameParameterValue") boolean expanded) {
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
