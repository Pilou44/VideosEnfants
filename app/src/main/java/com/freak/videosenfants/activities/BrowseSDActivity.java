package com.freak.videosenfants.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.browsing.VideoElement;
import com.freak.videosenfants.elements.browsing.VideoElementAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class BrowseSDActivity extends BrowseActivity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseSDActivity.class.getSimpleName();

    private ListView mListView;
    private VideoElementAdapter mAdapter;
    private Vector<File> mRoots;
    private VideoElement mRootElement;

    private final String[] mExtensions = {"avi" , "mkv", "wmv", "mpg", "mpeg", "mp4"};
    private final Set<String> mSet = new HashSet<>(Arrays.asList(mExtensions));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowseSDActivity.this.onBackPressed();
            }
        });

        getDialog().setContentView(R.layout.browse_sd_context_menu_layout);

        mRoots = getLocalRoots(this);

        mRootElement = new VideoElement(true, mRoot, mRoot, null, this);
        mCurrent = mRootElement;
        mAdapter = new VideoElementAdapter(this);

        mListView = (ListView)findViewById(R.id.listView);
        assert mListView != null;
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(this);
    }

    public static Vector<File> getLocalRoots(Context context) {
        Vector<File> result = new Vector<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int nbRoots = context.getResources().getInteger(R.integer.local_roots_number);
        for (int i = 0 ; i < nbRoots ; i++){
            boolean visible = prefs.getBoolean("local_browse_" + i + "_visible", false);
            boolean empty = prefs.getString("local_browse_" + i, "").length() == 0;
            if (visible && !empty) {
                File childrenFolder = new File(prefs.getString("local_browse_" + i, ""));
                if (childrenFolder.exists() && childrenFolder.isDirectory()) {
                    if (DEBUG) {
                        Log.i(TAG, "New root found: " + childrenFolder.getAbsolutePath());
                    }
                    result.add(childrenFolder);
                }
            }
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        mAdapter.clear();
        if (mCurrent.equals(mRootElement)) {
            addFilesToList(mRoots, mCurrent);
        }
        else {
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addFilesToList(Vector<File> filesVector, VideoElement parent) {
        Vector<File> allFiles = new Vector<>();
        for (int i = 0 ; i < filesVector.size() ; i++) {
            File[] files = filesVector.get(i).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    else {
                        try {
                            String extension = pathname.getName().substring(pathname.getName().lastIndexOf(".") + 1);
                            return mSet.contains(extension);
                        }
                        catch (Exception e) {
                            return false;
                        }
                    }
                }
            });
            allFiles.addAll(new HashSet<>(Arrays.asList(files)));
        }
        Vector<VideoElement> vector = sortFiles(allFiles, parent);
        mAdapter.clear();
        mAdapter.addAll(vector);
    }

    private void addFilesToList(File file, VideoElement parent) {
        Vector<File> fileVector = new Vector<>();
        fileVector.add(file);
        addFilesToList(fileVector, parent);
    }

    private Vector<VideoElement> sortFiles(Vector<File> entries, VideoElement parent) {
        Vector<VideoElement> directories = new Vector<>();
        Vector<VideoElement> files = new Vector<>();

        for (int i = 0; i < entries.size() ; i++) {
            if (entries.get(i).isDirectory()) {
                directories.add(new VideoElement(entries.get(i), parent, this));
            } else {
                files.add(new VideoElement(entries.get(i), parent, this, mListView));
            }
        }

        if (DEBUG) {
            Log.i(TAG, "" + directories.size() + " directories found");
            Log.i(TAG, "" + files.size() + " files found");
        }

        sort(directories);
        sort(files);

        if (DEBUG) {
            Log.i(TAG, "" + directories.size() + " directories after sorting");
            Log.i(TAG, "" + files.size() + " files after sorting");
        }

        Vector<VideoElement> allFiles = new Vector<>();
        for (int i = 0; i < directories.size(); i++) {
            allFiles.add(directories.get(i));
        }
        for (int i = 0; i < files.size(); i++) {
            allFiles.add(files.get(i));
        }
        return allFiles;
    }

    private void sort (Vector<VideoElement> files) {
        int longueur = files.size();
        VideoElement tampon;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (files.get(i).getName().compareToIgnoreCase(files.get((i+1)).getName()) > 0) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = files.get(i);
                    files.set(i, files.get(i + 1));
                    files.set(i+1, tampon);
                    permut = true;
                }
            }
        } while (permut);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoElement element = mAdapter.getItem(position);
        if (element.isDirectory()) {
            mCurrent = element;
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
            mAdapter.notifyDataSetChanged();
            mListView.setSelectionAfterHeaderView();
        }
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.getPath()));
            intent.setDataAndType(Uri.parse(element.getPath()), "video/*");
            startActivity(intent);
        }
    }

    protected void parseAndUpdate(VideoElement element) {
        if (element.getPath().equals(mRoot)) {
            if (DEBUG)
                Log.i(TAG, "Going to root");
            mCurrent = mRootElement;
            for (int i = 0 ; i < mRoots.size() ; i++) {
                addFilesToList(mRoots, mRootElement);
            }
        }
        else {
            if (DEBUG)
                Log.i(TAG, "Going to " + mCurrent.getParent().getPath());
            mCurrent = element;
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
            mAdapter.notifyDataSetChanged();
            mListView.setSelectionAfterHeaderView();
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelectionAfterHeaderView();
    }

    @Override
    protected void prepareContextMenu(final AdapterView<?> parent, final int position) {
        super.prepareContextMenu(parent, position);
        Button deleteButton = (Button) getDialog().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(BrowseSDActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(BrowseSDActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ApplicationSingleton.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                }

                final VideoElement element = mAdapter.getItem(position);
                if (DEBUG)
                    Log.i(TAG, "Delete " + element.getPath());
                getDialog().dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(BrowseSDActivity.this);
                builder.setTitle(getString(R.string.delete_title));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(element.getPath());
                        if (!file.isDirectory() || file.listFiles().length == 0) {
                            if (file.delete()) {
                                mAdapter.remove(element);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BrowseSDActivity.this);
                            builder.setTitle(getString(R.string.delete_dir_title));
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

}
