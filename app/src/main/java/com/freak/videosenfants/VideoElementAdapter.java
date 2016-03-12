package com.freak.videosenfants;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Guillaume BEGUIN on 07/03/2016.
 */
public class VideoElementAdapter extends ArrayAdapter<VideoElement> {

    private final Context mContext;

    public VideoElementAdapter(Context context, List<VideoElement> elements) {
        super(context, 0, elements);
        mContext = context;
    }

    public VideoElementAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_element,parent, false);
        }

        VideoElementHolder viewHolder = (VideoElementHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new VideoElementHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        VideoElement element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());
        viewHolder.icon.setImageDrawable(element.getIcon());

        return convertView;
    }

    private class VideoElementHolder{
        public TextView name;
        public ImageView icon;
    }
}
