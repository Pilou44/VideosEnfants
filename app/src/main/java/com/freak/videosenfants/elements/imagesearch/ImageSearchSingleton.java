package com.freak.videosenfants.elements.imagesearch;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ImageSearchSingleton {

    private static ImageSearchSingleton mInstance = new ImageSearchSingleton();
    //private RequestQueue mRequestQueue;
    //private Context mCtx;

    private ImageSearchSingleton() {
        //mCtx = context;
        //mRequestQueue = getRequestQueue();
    }

    public static synchronized ImageSearchSingleton getInstance() {
        return mInstance;
    }

    private RequestQueue getRequestQueue(Context context) {
        /*if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }*/
        return Volley.newRequestQueue(context.getApplicationContext());
    }

    public <T> void addToRequestQueue(Context context, Request<T> req) {
        getRequestQueue(context).add(req);
    }

}
