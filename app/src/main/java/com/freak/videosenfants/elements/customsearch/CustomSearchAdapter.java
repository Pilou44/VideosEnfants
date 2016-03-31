package com.freak.videosenfants.elements.customsearch;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.freak.videosenfants.R;

import java.util.List;

public class CustomSearchAdapter extends ArrayAdapter<SingleImage> {

    private static final boolean DEBUG = true;
    private static final String TAG = CustomSearchAdapter.class.getSimpleName();
    private final Context mContext;

    public CustomSearchAdapter(Context context, List<SingleImage> elements) {
        super(context, 0, elements);
        mContext = context;
    }

    public CustomSearchAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_element, parent, false);
        }

        ImageHolder viewHolder = (ImageHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ImageHolder();
            viewHolder.image1 = (ImageView) convertView.findViewById(R.id.image_1);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        final SingleImage element = getItem(position);
        viewHolder.image1.setImageDrawable(new BitmapDrawable(mContext.getResources(), element.getImage1()));

        return convertView;
    }

    private class ImageHolder{
        public ImageView image1;
    }

}
