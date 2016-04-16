package com.freak.videosenfants.elements.browsing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.elements.ApplicationSingleton;

import java.io.File;
import java.util.List;

public class DestSpinnerAdapter extends ArrayAdapter<File> {

    public DestSpinnerAdapter(Context context, List<File> list) {
        super(context, android.R.layout.simple_spinner_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item,parent, false);
        }

        FileHolder viewHolder = (FileHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new FileHolder();
            viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        File element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        String text = element.getAbsolutePath() + " (" + getContext().getString(R.string.free_space) + " " + ApplicationSingleton.getInstance(getContext()).formatByteSize(element.getFreeSpace()) + ")";
        viewHolder.name.setText(text);

        return convertView;
    }

    @Override
    public View getDropDownView (int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        }

        FileHolder viewHolder = (FileHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new FileHolder();
            viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        File element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        String text = element.getAbsolutePath() + " (" + getContext().getString(R.string.free_space) + " " + ApplicationSingleton.getInstance(getContext()).formatByteSize(element.getFreeSpace()) + ")";
        viewHolder.name.setText(text);

        return convertView;
    }

    private class FileHolder{
        public TextView name;
    }
}
