package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.freak.videosenfants.R;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

public class BrowseLocalPreference extends DialogPreference implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = BrowseLocalPreference.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final Pattern DIR_SEPARATOR = Pattern.compile("/");

    private final Context mContext;
    private String mDefaultValue = "";
    private ListView mListView;
    private Vector<FileElement> mAllFiles;
    private FileAdapter mAdapter;
    private TextView mValue;
    private boolean mVisible = true;
    private boolean mDeletable = false;
    private SharedPreferences mSharedPref;

    public BrowseLocalPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setLayoutResource(R.layout.preference_local);
        parseAttrs(attrs);
        setDialogLayoutResource(R.layout.local_preference_dialog);
    }

    public BrowseLocalPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.preference_local);
        parseAttrs(attrs);
        setDialogLayoutResource(R.layout.local_preference_dialog);
    }

    public BrowseLocalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setLayoutResource(R.layout.preference_local);
        parseAttrs(attrs);
        setDialogLayoutResource(R.layout.local_preference_dialog);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View preferenceView = super.onCreateView(parent);
        preferenceView.setOnClickListener(this);
        return preferenceView;
    }


    private void parseAttrs(AttributeSet attrs) {
        if (DEBUG)
            Log.i(TAG, "attributes:" + attrs.getAttributeCount());

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        for (int i = 0 ; i < attrs.getAttributeCount() ; i++) {
            if (DEBUG)
                Log.i(TAG, "attribute:" + attrs.getAttributeName(i));
            if (attrs.getAttributeName(i).equals("defaultValue")) {
                String temp = attrs.getAttributeValue(i);
                if (temp.startsWith("@")) {
                    try {
                        mDefaultValue = mContext.getString(Integer.parseInt(temp.substring(1)));
                    }
                    catch (Exception e) {
                        Log.w(TAG, "Error whiile decoding default value");
                    }
                }
                else {
                    mDefaultValue = attrs.getAttributeValue(i);
                }
                if (DEBUG)
                    Log.i(TAG, "default: " + mDefaultValue);
            }
            else if (attrs.getAttributeName(i).equals("visible")) {
                mVisible = mSharedPref.getBoolean(this.getKey() + "_visible", attrs.getAttributeBooleanValue(i, true));
            }
            else if (attrs.getAttributeName(i).equals("deletable")) {
                mDeletable = attrs.getAttributeBooleanValue(i, false);
            }
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageButton removeButton = (ImageButton) view.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(this);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(this.getTitle());
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        summaryView.setText(this.getSummary());
        TextView valueView = (TextView) view.findViewById(R.id.value);
        valueView.setText(mSharedPref.getString(this.getKey(), mDefaultValue));
        if (mVisible) {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
        if (mDeletable) {
            removeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(this.getKey(), mValue.getText().toString());
            editor.apply();
            this.notifyChanged();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mListView = (ListView)view.findViewById(R.id.list);
        mValue = (TextView)view.findViewById(R.id.value);

        mAllFiles = new Vector<>();
        String[] sdSources = getStorageDirectories();
        for (String sdSource : sdSources) {
            File file = new File(sdSource);
            FileElement element = new FileElement(file, file.getAbsolutePath());
            mAllFiles.add(element);
        }

        mAdapter = new FileAdapter(mContext, mAllFiles);

        mListView = (ListView)view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

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
                final String[] folders = DIR_SEPARATOR.split(path);
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
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if(!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
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
        mValue.setText(element.getFile().getAbsolutePath());
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.remove_button) {
            String key = getKey() + "_visible";
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(key, false);
            editor.apply();
        }
        else {
            super.onClick();
        }
    }

    public void setVisible(boolean visible) {
        if (DEBUG)
            Log.i(TAG, "refresh preference " + getKey());
        mVisible = visible;
        notifyChanged();
    }
}
