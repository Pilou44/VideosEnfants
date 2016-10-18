package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.freak.videosenfants.R;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

public class BrowseLocalPreference extends BrowsePreference implements AdapterView.OnItemClickListener {

    //private static final String TAG = BrowseLocalPreference.class.getSimpleName();
    //private static final boolean DEBUG = true;
    private static final Pattern DIR_SEPARATOR = Pattern.compile("/");

    private ListView mListView;
    private Vector<FileElement> mAllFiles;
    private FileAdapter mAdapter;
    private FileElement mSelectedElement;

    public BrowseLocalPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDialogLayoutResource(R.layout.local_preference_dialog);
    }

    public BrowseLocalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.local_preference_dialog);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && mSelectedElement != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putString(this.getKey(), mSelectedElement.getFile().getPath());
            editor.apply();
            this.notifyChanged();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mSelectedElement = null;

        mAllFiles = new Vector<>();
        String[] sdSources = getStorageDirectories();
        for (String sdSource : sdSources) {
            File file = new File(sdSource);
            FileElement element = new FileElement(file, file.getAbsolutePath());
            mAllFiles.add(element);
        }

        mAdapter = new FileAdapter(getContext(), mAllFiles);

        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

    private String[] getStorageDirectories()
    {
        // Final set of paths
        final Set<String> rv = new HashSet<>();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
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
                    final String[] folders = DIR_SEPARATOR.split(path);
                    final String lastFolder = folders[folders.length - 1];
                    boolean isDigit = false;
                    try
                    {
                        //noinspection ResultOfMethodCallIgnored
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
                } else {
                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                }
            }
            // Add all secondary storages
            if(!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                // All Secondary SD-CARDs split into array
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
        } else {
            File[] files = getContext().getExternalFilesDirs(null);
            for (File file : files) {
                String path = file.getAbsolutePath();
                rv.add(path.replaceAll("/Android/data/" + getContext().getPackageName() + "/files", ""));
            }
        }

        return rv.toArray(new String[rv.size()]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        FileElement element = (FileElement)mListView.getItemAtPosition(position);
        if (!element.isExpanded()) {
            File[] subDirs = element.getFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (subDirs != null) {
                sort(subDirs);
                for (int i = 0; i < subDirs.length; i++) {
                    mAllFiles.insertElementAt(new FileElement(subDirs[i], element.getIndent() + 1), position + i + 1);
                }
                mAdapter.notifyDataSetChanged();
                element.setExpanded(true);
            }
        }
        mAdapter.setSelectedElement(position);
        mAdapter.notifyDataSetChanged();
        mSelectedElement = element;
    }

    private void sort(File[] files) {
        int longueur = files.length;
        File tampon;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (files[i].getName().compareToIgnoreCase(files[i+1].getName()) > 0) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = files[i];
                    files[i] = files[i+1];
                    files[i+1] = tampon;
                    permut = true;
                }
            }
        } while (permut);
    }

}
