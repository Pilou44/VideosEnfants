package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class BrowseDlnaPreference extends DialogPreference {
    public BrowseDlnaPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BrowseDlnaPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrowseDlnaPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
