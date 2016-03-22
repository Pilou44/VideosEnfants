package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.freak.videosenfants.R;

public class AddButtonPreference extends Preference implements View.OnClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = AddButtonPreference.class.getSimpleName();

    private final Context mContext;
    private int mItemsNumber = 0;
    private SharedPreferences mSharedPref;

    public AddButtonPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setLayoutResource(R.layout.preference_add);
        parseAttrs(attrs);
    }

    public AddButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.preference_add);
        parseAttrs(attrs);
    }

    public AddButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setLayoutResource(R.layout.preference_add);
        parseAttrs(attrs);
    }

    private void parseAttrs(AttributeSet attrs) {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (DEBUG)
            Log.i(TAG, "attributes:" + attrs.getAttributeCount());

        for (int i = 0 ; i < attrs.getAttributeCount() ; i++) {
            if (DEBUG)
                Log.i(TAG, "attribute:" + attrs.getAttributeName(i));
            if (attrs.getAttributeName(i).equals("itemsNumber")) {
                String value = attrs.getAttributeValue(i);
                if (value.startsWith("@")) {
                    mItemsNumber = mContext.getResources().getInteger(Integer.parseInt(value.substring(1)));
                }
                else {
                    mItemsNumber = attrs.getAttributeIntValue(i, 0);
                }
                if (DEBUG)
                    Log.i(TAG, "Number of items: " + mItemsNumber);
            }
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int freeItem = getFreeItem();
        if (freeItem >= 0) {
            String tempKey = getKey() + "_" + freeItem + "_visible";
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(tempKey, true);
            editor.apply();
        }
    }

    private int getFreeItem() {
        int ret = -1;
        String key = this.getKey();
        for (int i = 0 ; i < mItemsNumber ; i++) {
            String tempKey = key + "_" + i + "_visible";
            if (DEBUG)
                Log.i(TAG, "Looking for key " + tempKey);
            if (!mSharedPref.getBoolean(tempKey, false)) {
                ret = i;
                if (DEBUG)
                    Log.i(TAG, tempKey + " is free");
                break;
            }
        }
        if (DEBUG && ret == -1)
            Log.i(TAG, "No item free");
        return ret;
    }
}
