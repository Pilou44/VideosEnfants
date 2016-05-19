package com.freak.videosenfants.elements.preferences;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.freak.videosenfants.R;

public class BrowsePreference extends DialogPreference implements View.OnKeyListener {

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

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView valueView = (TextView) view.findViewById(R.id.value);
        valueView.setText(mSharedPref.getString(this.getKey(), mDefaultValue));
    }

    public Context getContext() {
        return mContext;
    }

    @Override
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

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClosed(true);
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onDialogClosed(false);
            }
        });

        Button delete = (Button) dialog.findViewById(R.id.delete);
        if (mDeletable) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onDialogClosed(false);

                    String key = getKey() + mContext.getString(R.string.key_visible);
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean(key, false);
                    editor.apply();
                }
            });
        }
        else {
            delete.setVisibility(View.GONE);
        }

        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN){
            super.onClick();
            return true;
        }
        else {
            return false;
        }
    }
}
