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
public class DlnaElement extends BrowsableElement implements BaseElement {

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

    public DlnaElement(long id, String udn, String url, String path, int maxAge) {
        super(0);
        mId = id;
        mUdn = udn;
        mUrl = url;
        mPath = path;
        mMaxAge = maxAge;
    }

    @SuppressWarnings("WeakerAccess")
    public DlnaElement(String udn, String url, String path, int maxAge, String name, int indent) {
        super(indent);
        mUdn = udn;
        mUrl = url;
        mPath = path;
        mMaxAge = maxAge;
        mName = name;
    }

    public DlnaElement(Device device, RemoteService service) {
        super(0);
        mUdn = device.getIdentity().getUdn().getIdentifierString();
        mUrl = ((RemoteDeviceIdentity)device.getIdentity()).getDescriptorURL().toString();
        mMaxAge = device.getIdentity().getMaxAgeSeconds();
        mService = service;
        mPath = "0";
        mName = device.getDisplayString();
        Log.i(TAG, "New device " + mName);
    }

    public DlnaElement(String title, String path, DlnaElement parent) {
        super(parent.getIndent() + 1);
        mUdn = parent.getUdn();
        mUrl = parent.getUrl();
        mMaxAge = parent.getMaxAge();
        mService = parent.getService();
        mPath = path;
        mName = title;
    }

    @Override
    public String getName() {
        return mName;
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
