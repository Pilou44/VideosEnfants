package com.freak.videosenfants.elements.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.freak.videosenfants.R;

public class ImageSearchAdapter extends ArrayAdapter<Bitmap> {

    private final Context mContext;

    public ImageSearchAdapter(Context context) {
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
        final Bitmap element = getItem(position);
        viewHolder.image1.setImageDrawable(new BitmapDrawable(mContext.getResources(), element));

        return convertView;
    }

    private class ImageHolder{
        public ImageView image1;
    }

}
