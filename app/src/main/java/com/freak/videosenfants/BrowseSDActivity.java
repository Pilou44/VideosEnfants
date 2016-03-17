package com.freak.videosenfants;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

public class BrowseSDActivity extends BrowseActivity implements AdapterView.OnItemClickListener {

    private static final boolean DEBUG = true;
    private static final String TAG = BrowseSDActivity.class.getSimpleName();
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

    private ListView mListView;
    private Vector<VideoElement> mAllFiles;
    private VideoElementAdapter mAdapter;
    private Vector<File> mRoots;
    private VideoElement mRootElement;

    private String[] mExtensions = {"avi" , "mkv", "wmv", "mpg", "mpeg", "mp4"};
    private Set<String> mSet = new HashSet<>(Arrays.asList(mExtensions));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowseSDActivity.this.onBackPressed();
            }
        });

        mRoots = new Vector<>();
        String[] sdSources = getStorageDirectories();
        for (String sdSource : sdSources) {
            File childrenFolder = new File(sdSource, "Movies/Enfants");
            if (childrenFolder.exists() && childrenFolder.isDirectory()) {
                if (DEBUG) {
                    Log.i(TAG, "New root found: " + childrenFolder.getAbsolutePath());
                }
                mRoots.add(childrenFolder);
            }
        }

        mRootElement = new VideoElement(true, mRoot, mRoot, null, this);
        mCurrent = mRootElement;
        mAllFiles = new Vector<>();
        addFilesToList(mRoots, mCurrent);
        mAdapter = new VideoElementAdapter(this, mAllFiles);

        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void addFilesToList(Vector<File> filesVector, VideoElement parent) {
        for (int i = 0 ; i < filesVector.size() ; i++) {
            addFilesToList(filesVector.get(i), parent);
        }
        sortFiles();
    }

    private void addFilesToList(File file, VideoElement parent) {
        File[] files = file.listFiles(new FileFilter() {
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
        for (File file1 : files) {
            mAllFiles.add(new VideoElement(file1, parent, this));
        }
        sortFiles();
    }

    private void sortFiles() {
        Vector<VideoElement> directories = new Vector<>();
        Vector<VideoElement> files = new Vector<>();

        for (int i = 0; i < mAllFiles.size(); i++) {
            if (mAllFiles.get(i).isDirectory()) {
                directories.add(mAllFiles.get(i));
            } else {
                files.add(mAllFiles.get(i));
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

        mAllFiles.removeAllElements();
        for (int i = 0; i < directories.size(); i++) {
            mAllFiles.add(directories.get(i));
        }
        for (int i = 0; i < files.size(); i++) {
            mAllFiles.add(files.get(i));
        }
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

    /**
     * Raturns all available SD-Cards in the system (include emulated)
     *
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standart way to get it.
     * TODO: Test on future Android versions 4.4+
     *
     * @return paths to all available SD-Cards in the system (include emulated)
     */
    public static String[] getStorageDirectories()
        {
        // Final set of paths
        final Set<String> rv = new HashSet<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if(TextUtils.isEmpty(rawEmulatedStorageTarget))
        {
            // Device has physical external storage; use plain paths.
            if(TextUtils.isEmpty(rawExternalStorage))
            {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            }
            else
            {
                rv.add(rawExternalStorage);
            }
        }
        else
        {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                rawUserId = "";
            }
            else
            {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try
                {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                }
                catch(NumberFormatException ignored)
                {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if(TextUtils.isEmpty(rawUserId))
            {
                rv.add(rawEmulatedStorageTarget);
            }
            else
            {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
        {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoElement element = mAllFiles.get(position);
        if (element.isDirectory()) {
            mAllFiles.removeAllElements();
            mCurrent = element;
            addFilesToList(new File(element.getPath()), mCurrent);
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
        mAllFiles.removeAllElements();

        if (element.getPath().equals(mRoot)) {
            Log.i(TAG, "Parent = root");
            mCurrent = mRootElement;
            for (int i = 0 ; i < mRoots.size() ; i++) {
                addFilesToList(mRoots.get(i), mRootElement);
            }
        }
        else {
            Log.i(TAG, "Parent = " + mCurrent.getParent().getPath());
            mAllFiles.removeAllElements();
            mCurrent = element;
            addFilesToList(new File(element.getPath()), mCurrent.getParent());
            mAdapter.notifyDataSetChanged();
            mListView.setSelectionAfterHeaderView();
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelectionAfterHeaderView();
    }

}
