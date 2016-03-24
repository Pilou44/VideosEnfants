package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.freak.videosenfants.R;

public class BrowsePreference extends DialogPreference implements View.OnClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowsePreference.class.getSimpleName();
    private final Context mContext;
    private SharedPreferences mSharedPref;
    private String mDefaultValue = "";
    private boolean mVisible = true;
    private boolean mDeletable = false;

    public BrowsePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setLayoutResource(R.layout.preference_browse);
        parseAttrs(attrs);
    }

    public BrowsePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.preference_browse);
        parseAttrs(attrs);
    }

    public BrowsePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setLayoutResource(R.layout.preference_browse);
        parseAttrs(attrs);
    }

    private void parseAttrs(AttributeSet attrs) {
        if (DEBUG)
            Log.i(TAG, "attributes:" + attrs.getAttributeCount());

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        for (int i = 0 ; i < attrs.getAttributeCount() ; i++) {
            if (DEBUG)
                Log.i(TAG, "attribute:" + attrs.getAttributeName(i));
            if (attrs.getAttributeName(i).equals("defaultValue")) {
                String temp = attrs.getAttributeValue(i);
                if (temp.startsWith("@")) {
                    try {
                        mDefaultValue = mContext.getString(Integer.parseInt(temp.substring(1)));
                    }
                    catch (Exception e) {
                        Log.w(TAG, "Error whiile decoding default value");
                    }
                }
                else {
                    mDefaultValue = attrs.getAttributeValue(i);
                }
                if (DEBUG)
                    Log.i(TAG, "default: " + mDefaultValue);
            }
            else if (attrs.getAttributeName(i).equals("visible")) {
                mVisible = mSharedPref.getBoolean(this.getKey() + "_visible", attrs.getAttributeBooleanValue(i, true));
            }
            else if (attrs.getAttributeName(i).equals("deletable")) {
                mDeletable = attrs.getAttributeBooleanValue(i, false);
            }
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View preferenceView = super.onCreateView(parent);
        preferenceView.setOnClickListener(this);
        return preferenceView;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageButton removeButton = (ImageButton) view.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(this);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(this.getTitle());
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        if (getSummary() != null && getSummary().length() > 0) {
            summaryView.setText(getSummary());
        }
        else {
            summaryView.setVisibility(View.GONE);
        }
        TextView valueView = (TextView) view.findViewById(R.id.value);
        valueView.setText(mSharedPref.getString(this.getKey(), ""));
        if (mVisible) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
        if (mDeletable) {
            removeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.remove_button) {
            String key = getKey() + "_visible";
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(key, false);
            editor.apply();
        }
        else {
            super.onClick();
        }
    }

    public void setVisible(boolean visible) {
        if (DEBUG)
            Log.i(TAG, "refresh preference " + getKey());
        mVisible = visible;
        notifyChanged();
    }

    public Context getContext() {
        return mContext;
    }
}
