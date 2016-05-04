package com.freak.videosenfants.elements.imagesearch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SearchAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    private static final String TAG = SearchAsyncTask.class.getSimpleName();

    private final String mSearchStr;
    private final Callback mCallback;
    private Error mError;

    public SearchAsyncTask(String searchStr, Callback callback) {
        mSearchStr = searchStr;
        mCallback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject mainObject = null;
        try {
            String searchStr = URLEncoder.encode(mSearchStr, "utf-8");
            String qwantUrl = "https://api.qwant.com/egp/search/images?count=30&q=" + searchStr;

            URL url = new URL(qwantUrl);

            URLConnection urlConnection = url.openConnection();
            InputStream response = urlConnection.getInputStream();
            String res = readStream(response);

            mainObject = new JSONObject(res);
            response.close();

        } catch (Exception e) {
            e.printStackTrace();
            mError = new Error(e.getMessage(), e);
            Log.e(TAG, e.getMessage());
        }

        return mainObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        if (mCallback != null) {
            mCallback.onComplete(result, mError);
        }
    }

    private String readStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public interface Callback {
        void onComplete(JSONObject o, @SuppressWarnings("UnusedParameters") Error error);
    }
}
