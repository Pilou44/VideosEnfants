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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.imagesearch.CustomSearchAdapter;
import com.freak.videosenfants.elements.imagesearch.CustomSearchSingleton;
import com.freak.videosenfants.elements.imagesearch.SingleImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ImageSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = ImageSearchActivity.class.getSimpleName();
    private static final String CUSTOM_SEARCH_KEY = "AIzaSyA3A0UsXI6lgAGVxtKsT2XLAh-ahDbElTk";
    private static final String CUSTOM_SEARCH_CX = "003716044463688473868%3A-2hh_9a9o0u";

    private CustomSearchAdapter mAdapter;
    private Toolbar mToolbar;

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
        String name = getIntent().getStringExtra("name");
        if (DEBUG) {
            Log.i(TAG, "New search: " + search);
            Log.i(TAG, "Name: " + name);
        }

        getImages(search, name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ApplicationSingleton.getInstance(this).isParentMode())
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorParent));
        else
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void getImages(String search, final String name) {
        String query = "";
        try {
            query = URLEncoder.encode(search, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://www.googleapis.com/customsearch/v1?cx=" + CUSTOM_SEARCH_CX + "&searchType=image&key=" + CUSTOM_SEARCH_KEY + "&q=" + query;
        if (DEBUG) {
            Log.i(TAG, "URL: " + url);
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Result: " + response.toString());
                        try {
                            JSONArray items = response.getJSONArray("items");
                            if (DEBUG)
                                Log.i(TAG, "" + items.length() + " images found");
                            if (items.length() > 0) {
                                for (int i = 0 ; i < items.length() ; i++) {
                                    if (DEBUG)
                                        Log.i(TAG, "New item: " + items.getJSONObject(i).getString("title") + ", " + items.getJSONObject(i).getString("link"));
                                    ImageRequest request = new ImageRequest(items.getJSONObject(i).getString("link"),
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {
                                                SingleImage image = new SingleImage(bitmap, name);
                                                mAdapter.add(image);
                                                mAdapter.notifyDataSetChanged();
                                                //mImageView.setImageBitmap(bitmap);
                                            }
                                        }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                                        new Response.ErrorListener() {
                                            public void onErrorResponse(VolleyError error) {
                                                SingleImage image = new SingleImage(null, null);
                                                mAdapter.add(image);
                                                mAdapter.notifyDataSetChanged();
                                                //mImageView.setImageResource(R.drawable.image_load_error);
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
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error: " + error.getMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        CustomSearchSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SingleImage image = mAdapter.getItem(position);
        saveBitmapToFile(image);
        onBackPressed();
    }

    private void saveBitmapToFile(SingleImage image) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String dir = prefs.getString("local_pictures", this.getString(R.string.default_local_pictures));
        String fileName = image.getName() + ".jpg";
        File imageFile = new File(dir,fileName);

        Bitmap bm = image.getImage1();

        if (DEBUG) {
            Log.i(TAG, "Reduce image size");
        }
        int pxWidth = getResources().getDimensionPixelSize(R.dimen.thumbnail_width);
        int pxHeight = getResources().getDimensionPixelSize(R.dimen.thumbnail_height);
        int srcWidth = bm.getWidth();
        int srcHeight = bm.getHeight();
        int destWidth;
        int destHeight;

        destWidth = pxWidth;
        destHeight = (srcHeight * pxWidth) / srcWidth;

        if (destHeight > pxHeight){
            destHeight = pxHeight;
            destWidth = (srcWidth * pxHeight) / srcHeight;
        }
        Bitmap bmp = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bm, destWidth, destHeight, false)).getBitmap();
        bm.recycle();

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
}
