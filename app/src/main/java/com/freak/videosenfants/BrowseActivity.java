package com.freak.videosenfants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.freak.videosenfants.elements.ApplicationSingleton;

public abstract class BrowseActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    protected VideoElement mCurrent;
    protected String mRoot = "root";

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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
        if (ApplicationSingleton.getInstance(this).isParentMode()) {
            final VideoElement element = (VideoElement) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, ImageSearchActivity.class);
            String search = element.getName();
            VideoElement parentElement = element.getParent();
            while (parentElement != null && parentElement.getParent() != null) {
                search += " " + parentElement.getName();
                parentElement = parentElement.getParent();
            }
            intent.putExtra("search", search);
            intent.putExtra("name", element.getName());
            startActivity(intent);
            return true;
        }
        else {
            return false;
        }
    }

    protected abstract void parseAndUpdate(VideoElement parent);

}
