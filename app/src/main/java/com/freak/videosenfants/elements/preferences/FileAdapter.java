package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.domain.bean.FileElement;

import java.util.List;

class FileAdapter extends ArrayAdapter<FileElement> {
    private int mSelectedElement;

    public FileAdapter(Context context, List<FileElement> elements) {
        super(context, 0, elements);
        mSelectedElement = -1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_element, parent, false);
        }

        FileHolder viewHolder = (FileHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new FileHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        FileElement element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());

        int padding = viewHolder.layout.getPaddingRight();
        viewHolder.layout.setPadding(
                padding * (element.getIndent() + 1),
                padding, padding, padding);

        if (position == mSelectedElement) {
            convertView.setBackgroundColor(0x66666666);
        }
        else {
            convertView.setBackgroundColor(0x00ffffff);
        }

        return convertView;
    }

    public void setSelectedElement(int selectedElement) {
        mSelectedElement = selectedElement;
    }

    private class FileHolder{
        public TextView name;
        public RelativeLayout layout;
    }
}
