package com.freak.videosenfants.elements.preferences;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.freak.videosenfants.R;

public class BrowsePreference extends DialogPreference implements View.OnClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowsePreference.class.getSimpleName();
    private final Context mContext;
    private SharedPreferences mSharedPref;
    private String mDefaultValue = "";
    private boolean mDeletable = false;

    @SuppressWarnings("WeakerAccess")
    public BrowsePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.preference_browse);
        parseAttrs(attrs);
    }

    @SuppressWarnings("WeakerAccess")
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
                        Log.w(TAG, "Error while decoding default value");
                    }
                }
                else {
                    mDefaultValue = attrs.getAttributeValue(i);
                }
                if (DEBUG)
                    Log.i(TAG, "default: " + mDefaultValue);
            }
            else if (attrs.getAttributeName(i).equals("deletable")) {
                mDeletable = attrs.getAttributeBooleanValue(i, false);
            }
        }
    }

    /*@Override
    protected View onCreateView(ViewGroup parent) {
        View preferenceView = super.onCreateView(parent);
        preferenceView.setOnClickListener(this);
        return preferenceView;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        ImageButton removeButton = (ImageButton) view.findViewById(R.id.remove_button);
        TextView valueView = (TextView) view.findViewById(R.id.value);

        valueView.setText(mSharedPref.getString(this.getKey(), mDefaultValue));
        if (mDeletable) {
            removeButton.setVisibility(View.VISIBLE);
            removeButton.setOnClickListener(this);
        }
        else {
            removeButton.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.remove_button) {
            String key = getKey() + mContext.getString(R.string.key_visible);
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(key, false);
            editor.apply();
        }
        else {
            super.onClick();
        }
    }

    public Context getContext() {
        return mContext;
    }

    /*@Override
    protected void showDialog(Bundle state) {
        final Dialog dialog = new Dialog(mContext);

        dialog.setTitle(getDialogTitle());
        View contentView = dialog.getLayoutInflater().inflate(getDialogLayoutResource(), null);
        dialog.setContentView(contentView);

        onBindDialogView(contentView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        Button ok = (Button) dialog.findViewById(R.id.button1);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClosed(true);
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClosed(false);
            }
        });

        dialog.setOnDismissListener(this);
        dialog.show();
    }*/

}
