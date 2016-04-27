package com.freak.videosenfants.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.imagesearch.CustomSearchAdapter;
import com.freak.videosenfants.elements.imagesearch.CustomSearchSingleton;
import com.freak.videosenfants.elements.imagesearch.SearchAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchAsyncTask.Callback {

    private static final boolean DEBUG = true;
    private static final String TAG = ImageSearchActivity.class.getSimpleName();

    private CustomSearchAdapter mAdapter;
    private Toolbar mToolbar;
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSearchActivity.this.onBackPressed();
            }
        });

        mAdapter = new CustomSearchAdapter(this);

        GridView grid = (GridView) findViewById(R.id.grid_view);
        assert grid != null;
        grid.setAdapter(mAdapter);
        grid.setOnItemClickListener(this);

        String search = getIntent().getStringExtra("search");
        mName = getIntent().getStringExtra("name");
        if (DEBUG) {
            Log.i(TAG, "New search: " + search);
            Log.i(TAG, "Name: " + mName);
        }

        SearchAsyncTask task = new SearchAsyncTask(search, 20, this);
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ApplicationSingleton.getInstance(this).isParentMode())
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorParent));
        else
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bitmap image = mAdapter.getItem(position);
        saveBitmapToFile(image);
        onBackPressed();
    }

    private void saveBitmapToFile(Bitmap image) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String dir = prefs.getString("local_pictures", this.getString(R.string.default_local_pictures));
        String fileName = mName + ".jpg";
        File imageFile = new File(dir,fileName);

        if (DEBUG) {
            Log.i(TAG, "Reduce image size");
        }
        int pxWidth = getResources().getDimensionPixelSize(R.dimen.thumbnail_width);
        int pxHeight = getResources().getDimensionPixelSize(R.dimen.thumbnail_height);
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int destWidth;
        int destHeight;

        destWidth = pxWidth;
        destHeight = (srcHeight * pxWidth) / srcWidth;

        if (destHeight > pxHeight){
            destHeight = pxHeight;
            destWidth = (srcWidth * pxHeight) / srcHeight;
        }
        Bitmap bmp = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(image, destWidth, destHeight, false)).getBitmap();
        image.recycle();

        if (DEBUG) {
            Log.i(TAG, "Save file to " + imageFile.getPath());
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.close();
        }
        catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        finally {
            bmp.recycle();
        }
    }

    @Override
    public void onComplete(JSONObject response, Error error) {
        if (response != null) {
            try {
                JSONObject d = response.getJSONObject("d");

                JSONArray items = d.getJSONArray("results");
                if (DEBUG)
                    Log.i(TAG, "" + items.length() + " images found");
                if (items.length() > 0) {
                    for (int i = 0; i < items.length(); i++) {
                        if (DEBUG)
                            Log.i(TAG, "New item: " + items.getJSONObject(i).getString("Title") + ", " + items.getJSONObject(i).getString("MediaUrl"));
                        ImageRequest request = new ImageRequest(items.getJSONObject(i).getString("MediaUrl"),
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        mAdapter.add(bitmap);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        mAdapter.add(null);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                        // Access the RequestQueue through your singleton class.
                        CustomSearchSingleton.getInstance(ImageSearchActivity.this).addToRequestQueue(request);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
