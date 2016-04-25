package com.freak.videosenfants.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.freak.videosenfants.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView modulesView = (TextView) findViewById(R.id.modules);

        String modules = getString(R.string.modules_list);
        modules += "- Cling (http://4thline.org/projects/cling/)\n";
        modules += "- FFmpeg (https://www.ffmpeg.org/)\n";
        modules += "- FFmpegMediaMetadataRetriever (https://github.com/wseemann/FFmpegMediaMetadataRetriever)\n";
        modules += "- httpcore (https://hc.apache.org/httpcomponents-core-ga/)\n";
        modules += "- Universal Image Loader (https://github.com/nostra13/Android-Universal-Image-Loader)\n";

        assert modulesView != null;
        modulesView.setText(modules);
    }

}
