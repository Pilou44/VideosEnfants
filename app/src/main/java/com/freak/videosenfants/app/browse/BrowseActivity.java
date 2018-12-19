package com.freak.videosenfants.app.browse;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.freak.videosenfants.R;
import com.freak.videosenfants.activities.ImageSearchActivity;
import com.freak.videosenfants.app.core.BaseActivity;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.domain.bean.VideoElement;

public abstract class BrowseActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {

    protected VideoElement mCurrent;
    protected String mRoot = "root";
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance().isParentMode(this))
            setTheme(R.style.AppTheme_ParentMode_NoActionBar);
        else
            setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);

        mDialog = new Dialog(this);
        mDialog.setTitle(R.string.browse_context_menu_title);
        mDialog.setContentView(R.layout.browse_context_menu_layout);
        mDialog.setCanceledOnTouchOutside(false);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ApplicationSingleton.MY_PERMISSIONS_REQUEST_READ_STORAGE);

        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrent == null || mCurrent.getPath().equals(mRoot)) {
            super.onBackPressed();
        }
        else {
            parseAndUpdate(mCurrent.getParent());
        }
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id){
        if (ApplicationSingleton.getInstance().isParentMode(this)) {
            prepareContextMenu(parent, position);
            mDialog.show();
            return true;
        }
        else {
            return false;
        }
    }

    protected void prepareContextMenu(final AdapterView<?> parent, final int position) {
        Button imageButton = (Button) mDialog.findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(BrowseActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(BrowseActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ApplicationSingleton.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                }

                final VideoElement element = (VideoElement) parent.getItemAtPosition(position);
                Intent intent = new Intent(BrowseActivity.this, ImageSearchActivity.class);
                String search = element.getName();
                VideoElement parentElement = element.getParent();
                while (parentElement != null && parentElement.getParent() != null) {
                    search += " " + parentElement.getName();
                    parentElement = parentElement.getParent();
                }
                intent.putExtra("search", search);
                intent.putExtra("name", element.getName());
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    protected Dialog getDialog() {
        return mDialog;
    }

    protected abstract void parseAndUpdate(VideoElement parent);

}
