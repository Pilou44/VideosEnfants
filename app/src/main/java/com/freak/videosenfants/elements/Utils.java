package com.freak.videosenfants.elements;

import android.app.Activity;
import android.content.Intent;

public class Utils{
    public static void restart(Activity activity)
    {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
}