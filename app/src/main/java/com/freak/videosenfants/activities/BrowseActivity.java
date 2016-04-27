package com.freak.videosenfants.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;
import com.freak.videosenfants.elements.browsing.VideoElement;

public abstract class BrowseActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    VideoElement mCurrent;
    String mRoot = "root";
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ApplicationSingleton.getInstance(this).isParentMode())
            setTheme(R.style.AppTheme_ParentMode_NoActionBar);
        else
            setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        mDialog = new Dialog(this);
        mDialog.setTitle(R.string.browse_context_menu_title);
        mDialog.setContentView(R.layout.browse_context_menu_layout);
        mDialog.setCanceledOnTouchOutside(false);
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
        if (ApplicationSingleton.getInstance(this).isParentMode()) {
            prepareContextMenu(parent, position);
            mDialog.show();
            return true;
        }
        else {
            return false;
        }
    }

    void prepareContextMenu(final AdapterView<?> parent, final int position) {
        Button imageButton = (Button) mDialog.findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    Dialog getDialog() {
        return mDialog;
    }

    protected abstract void parseAndUpdate(VideoElement parent);

}
