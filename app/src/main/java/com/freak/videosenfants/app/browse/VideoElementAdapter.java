package com.freak.videosenfants.app.browse;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class VideoElementAdapter extends ArrayAdapter<VideoElement> {

    private final ImageLoader mImageLoader;
    private final DisplayImageOptions mDirectoryOptions, mFileOptions;
    private final Handler mHandler;

    public VideoElementAdapter(Context context) {
        super(context, 0);

        mHandler = new Handler();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(context.getResources().getDimensionPixelSize(R.dimen.thumbnail_width), context.getResources().getDimensionPixelSize(R.dimen.thumbnail_height)) // default = device screen dimensions
                .diskCacheExtraOptions(context.getResources().getDimensionPixelSize(R.dimen.thumbnail_width), context.getResources().getDimensionPixelSize(R.dimen.thumbnail_height), null)
                .build();
        ImageLoader.getInstance().init(config);

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

        mDirectoryOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_animation)
                .showImageForEmptyUri(R.drawable.empty)
                .showImageOnFail(R.drawable.empty)
                .build();

        mFileOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_animation)
                .showImageForEmptyUri(R.drawable.loading_animation)
                .showImageOnFail(R.drawable.fichier)
                .build();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_element,parent, false);
        }

        VideoElementHolder viewHolder = (VideoElementHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new VideoElementHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.subIcon = (ImageView) convertView.findViewById(R.id.sub_icon);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        VideoElement element = getItem(position);
        assert element != null;

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());
        viewHolder.icon.setTag(element.getName());
        viewHolder.subIcon.setTag(element.getName());
        if (element.isDirectory()) {
            viewHolder.subIcon.setVisibility(View.VISIBLE);
            mImageLoader.displayImage("drawable://" + R.drawable.dossier, viewHolder.icon);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.subIcon, mDirectoryOptions);
        }
        else {
            viewHolder.subIcon.setVisibility(View.GONE);
            element.setPosition(position, mHandler, mImageLoader, mFileOptions);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.icon, mFileOptions);
        }

        return convertView;
    }

    private class VideoElementHolder{
        TextView name;
        ImageView icon;
        ImageView subIcon;
    }
}
