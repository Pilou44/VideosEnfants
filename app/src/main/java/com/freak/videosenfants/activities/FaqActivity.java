package com.freak.videosenfants.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;

public class FaqActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance(this).isParentMode())
            setTheme(R.style.AppTheme_ParentMode_NoActionBar);
        else
            setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button1 = (Button) findViewById(R.id.configure_local);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.configure_upnp);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.copy_to_local);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.find_thumbnail);
        button4.setOnClickListener(this);
        Button button5 = (Button) findViewById(R.id.delete_local);
        button5.setOnClickListener(this);
        Button button6 = (Button) findViewById(R.id.parent_mode);
        button6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(getString(R.string.key_id), v.getId());
        startActivity(intent);
    }
}
