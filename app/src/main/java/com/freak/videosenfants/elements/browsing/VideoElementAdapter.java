package com.freak.videosenfants.elements.browsing;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freak.videosenfants.R;

import java.util.List;
import java.util.Vector;

public class VideoElementAdapter extends ArrayAdapter<VideoElement> {

    private static final boolean DEBUG = true;
    private static final String TAG = VideoElementAdapter.class.getSimpleName();

    private final Context mContext;
    private final Vector<MyAsync> tasks;

    public VideoElementAdapter(Context context, List<VideoElement> elements) {
        super(context, 0, elements);
        mContext = context;
        tasks = new Vector<>();
    }

    public VideoElementAdapter(Context context) {
        super(context, 0);
        mContext = context;
        tasks = new Vector<>();
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
            viewHolder.subIcon = (ImageView) convertView.findViewById(R.id.sub_icon);
            viewHolder.animation = (ImageView) convertView.findViewById(R.id.animation);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        VideoElement element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());
        if (element.isDirectory()) {
            viewHolder.subIcon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.dossier, null));
            if (element.getIcon() != null) {
                viewHolder.subIcon.setImageDrawable(element.getIcon());
            }
            else {
                viewHolder.animation.setVisibility(View.VISIBLE);
                final VideoElementHolder finalViewHolder = viewHolder;
                viewHolder.animation.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable) finalViewHolder.animation.getBackground();
                        frameAnimation.start();
                    }
                });
                viewHolder.subIcon.setImageDrawable(mContext.getDrawable(R.drawable.empty));

                if (DEBUG)
                    Log.i(TAG, "Create new task for " + element.getName() +" in view " + viewHolder.subIcon);

                viewHolder.subIcon.setTag(element);
                MyAsync task = new MyAsync(viewHolder.subIcon, viewHolder.animation);
                task.execute(element);
                tasks.add(task);

                if (DEBUG)
                    Log.i(TAG, tasks.size() + " tasks launched");
            }
        }
        else {
            viewHolder.subIcon.setVisibility(View.GONE);
            if (element.getIcon() != null) {
                viewHolder.icon.setImageDrawable(element.getIcon());
            }
            else {
                viewHolder.animation.setVisibility(View.VISIBLE);
                final VideoElementHolder finalViewHolder = viewHolder;
                viewHolder.animation.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable) finalViewHolder.animation.getBackground();
                        frameAnimation.start();
                    }
                });
                viewHolder.icon.setImageDrawable(mContext.getDrawable(R.drawable.fichier));

                if (DEBUG)
                    Log.i(TAG, "Create new task for " + element.getName());

                viewHolder.icon.setTag(element);
                MyAsync task = new MyAsync(viewHolder.icon, viewHolder.animation);
                task.execute(element);
                tasks.add(task);

                if (DEBUG)
                    Log.i(TAG, tasks.size() + " tasks launched");
            }
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (DEBUG)
            Log.i(TAG, "" + tasks.size() + " tasks have been launched");
        for (int i = 0 ; i < tasks.size() ; i++) {
            if (tasks.get(i).getStatus().equals(AsyncTask.Status.RUNNING)){
                tasks.get(i).cancel(true);
            }
        }
        tasks.removeAllElements();
        if (DEBUG)
            Log.i(TAG, "All tasks removed");
        super.notifyDataSetChanged();
    }

    private class VideoElementHolder{
        public TextView name;
        public ImageView icon;
        public ImageView subIcon;
        public ImageView animation;
    }

    public class MyAsync extends AsyncTask<VideoElement, Void, VideoElement> {

        private final ImageView mView;
        private final ImageView mAnimation;

        public Object getPath() {
            return mPath;
        }

        private final Object mPath;

        public MyAsync(ImageView view, ImageView animation) {
            mView = view;
            mPath = view.getTag();
            mAnimation = animation;
        }

        @Override
        protected VideoElement doInBackground(VideoElement... element) {
            if (DEBUG)
                Log.i(TAG, "Load image for " + element[0].getName() + " in view " + mView);
            element[0].generateScreenshot();
            return element[0];
            //return ThumbnailUtils.createVideoThumbnail(objectURL[0], Thumbnails.MINI_KIND);
            //return ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(objectURL[0], MediaStore.Images.Thumbnails.MINI_KIND), 100, 100);
            //return generateScreenshot(element[0]);
        }

        @Override
        protected void onPostExecute(VideoElement element){

            if (!mView.getTag().equals(mPath)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                return;
            }

            if (DEBUG)
                Log.i(TAG, "Update view " + mView + " for " + element.getName());
            mView.setImageDrawable(element.getIcon());
            mAnimation.setVisibility(View.GONE);
        }

    }
}
