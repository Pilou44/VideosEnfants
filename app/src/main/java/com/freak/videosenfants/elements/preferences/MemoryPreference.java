package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;

import java.io.File;

public class MemoryPreference extends DialogPreference {
    private File mDirectory;

    public MemoryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_memory);
        setDialogTitle(getTitle());
        setDialogMessage(R.string.memory_pref_message);
        setPositiveButtonText(R.string.yes);
        setNegativeButtonText(R.string.no);
        parseAttrs(attrs);
    }

    public MemoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_memory);
        setDialogTitle(getTitle());
        setDialogMessage(R.string.memory_pref_message);
        setPositiveButtonText(R.string.yes);
        setNegativeButtonText(R.string.no);
        parseAttrs(attrs);
    }

    private void parseAttrs(AttributeSet attrs) {
        for (int i = 0 ; i < attrs.getAttributeCount() ; i++) {
            if (attrs.getAttributeName(i).equals("title")) {
                String value = attrs.getAttributeValue(i);
                if (value.startsWith("@")) {
                    value = getContext().getResources().getString(Integer.parseInt(value.substring(1)));
                }
                setTitle(value);
            }
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        updateSummary();

        /*Button clearButton = (Button) view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDirectory != null && mDirectory.exists() && mDirectory.isDirectory()) {
                    File[] allFiles = mDirectory.listFiles();
                    for (File allFile : allFiles) {
                        //noinspection ResultOfMethodCallIgnored
                        allFile.delete();
                    }

                    updateSummary();
                }
            }
        });*/
    }

    private void updateSummary() {
        int nbFiles = 0;
        int size = 0;
        if (mDirectory != null && mDirectory.exists() && mDirectory.isDirectory()) {
            File[] allFiles = mDirectory.listFiles();
            nbFiles = allFiles.length;
            for (File allFile : allFiles) {
                size += allFile.length();
            }
        }
        String text = nbFiles + " " + getContext().getString(R.string.files_coma) + " " + ApplicationSingleton.getInstance(getContext()).formatByteSize(size);
        setSummary(text);
    }

    public void setDirectory(File directory) {
        mDirectory = directory;
        updateSummary();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            if (mDirectory != null && mDirectory.exists() && mDirectory.isDirectory()) {
                File[] allFiles = mDirectory.listFiles();
                for (File allFile : allFiles) {
                    //noinspection ResultOfMethodCallIgnored
                    allFile.delete();
                }

                updateSummary();
            }
        }
    }
}
