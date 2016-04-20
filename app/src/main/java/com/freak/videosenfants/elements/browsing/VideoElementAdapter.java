package com.freak.videosenfants.elements.browsing;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    @SuppressWarnings("WeakerAccess")
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
        viewHolder.icon.setTag(element.getName());
        viewHolder.subIcon.setTag(element.getName());
        if (element.isDirectory()) {
            viewHolder.subIcon.setVisibility(View.VISIBLE);
            setImage(viewHolder.icon, convertView.getContext().getResources().getDrawable(R.drawable.dossier, null));
            if (element.getIcon() != null) {
                final VideoElementHolder finalViewHolder = viewHolder;
                viewHolder.animation.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable) finalViewHolder.animation.getBackground();
                        frameAnimation.stop();
                    }
                });
                viewHolder.animation.setVisibility(View.GONE);
                setImage(viewHolder.subIcon, element.getIcon());
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
                setImage(viewHolder.subIcon, mContext.getDrawable(R.drawable.empty));

                if (DEBUG)
                    Log.i(TAG, "Create new task for " + element.getName() + " at position " + position);

                MyAsync task = new MyAsync(viewHolder.subIcon, viewHolder.animation, tasks.size());
                //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, element);
                task.execute(element);
                tasks.add(task);

                if (DEBUG)
                    Log.i(TAG, tasks.size() + " tasks launched");
            }
        }
        else {
            viewHolder.subIcon.setVisibility(View.GONE);
            if (element.getIcon() != null) {
                final VideoElementHolder finalViewHolder = viewHolder;
                viewHolder.animation.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable) finalViewHolder.animation.getBackground();
                        frameAnimation.stop();
                    }
                });
                viewHolder.animation.setVisibility(View.GONE);
                setImage(viewHolder.icon, element.getIcon());
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
                setImage(viewHolder.icon, mContext.getDrawable(R.drawable.fichier));

                if (DEBUG)
                    Log.i(TAG, "Create new task for " + element.getName() + " at position " + position);

                MyAsync task = new MyAsync(viewHolder.icon, viewHolder.animation, tasks.size());
                //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, element);
                task.execute(element);
                tasks.add(task);

                if (DEBUG)
                    Log.i(TAG, tasks.size() + " tasks launched");
            }
        }

        return convertView;
    }

    @Override
    public void clear() {
        for(int i = 0 ; i < getCount() ; i++) {
            //noinspection EmptyCatchBlock
            try {
                BitmapDrawable image = ((BitmapDrawable) getItem(i).getIcon());
                if (image != mContext.getDrawable(R.drawable.fichier) &&
                        image != mContext.getDrawable(R.drawable.dossier)) {
                    image.getBitmap().recycle();
                    if (DEBUG)
                        Log.i(TAG, "Bitmap for " + getItem(i).getName() + " has been recycled");
                }
            } catch (NullPointerException | ClassCastException e) {
            }
        }
        super.clear();
    }

    @Override
    public void remove(VideoElement object) {
        super.remove(object);

        //noinspection EmptyCatchBlock
        try {
            BitmapDrawable image = ((BitmapDrawable) object.getIcon());
            if (image != mContext.getDrawable(R.drawable.fichier) &&
                    image != mContext.getDrawable(R.drawable.dossier)) {
                image.getBitmap().recycle();
                if (DEBUG)
                    Log.i(TAG, "Bitmap for " + object.getName() + " has been recycled");
            }
        } catch (NullPointerException | ClassCastException e) {
        }
    }

    private void setImage(ImageView icon, Drawable drawable) {
        icon.setImageDrawable(drawable);
    }

    @Override
    public void notifyDataSetChanged() {
        if (DEBUG)
            Log.i(TAG, "" + tasks.size() + " tasks have been launched");
        for (int i = 0 ; i < tasks.size() ; i++) {
            if (!tasks.get(i).getStatus().equals(AsyncTask.Status.FINISHED)){
                if (DEBUG) {
                    Log.i(TAG, "Cancel task " + i);
                }
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
        private final int mNumber;

        public MyAsync(ImageView view, ImageView animation, int number) {
            mView = view;
            mAnimation = animation;
            mNumber = number;
        }

        @Override
        protected VideoElement doInBackground(VideoElement... element) {
            if (DEBUG)
                Log.i(TAG, "Task " + mNumber + ": Load image for " + element[0].getName());
            element[0].generateScreenshot();
            return element[0];
        }

        @Override
        protected void onPostExecute(VideoElement element){
            if (DEBUG)
                Log.i(TAG, "Task " + mNumber + ": View tag is " + mView.getTag());

            if (mView.getTag().equals(element.getName())) {
                if (DEBUG)
                    Log.i(TAG, "Task " + mNumber + ": Update view for " + element.getName());

                mAnimation.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimationDrawable frameAnimation = (AnimationDrawable) mAnimation.getBackground();
                        frameAnimation.stop();
                    }
                });
                mAnimation.setVisibility(View.GONE);
                setImage(mView, element.getIcon());
            }
            else {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                if (DEBUG)
                    Log.i(TAG, "Task " + mNumber + ": Cancel update view for " + element.getName());
            }
        }

    }
}
