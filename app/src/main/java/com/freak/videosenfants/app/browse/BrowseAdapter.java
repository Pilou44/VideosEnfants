package com.freak.videosenfants.app.browse;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.browse.local.BrowseLocalContract;
import com.freak.videosenfants.domain.bean.VideoElement;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.VideoElementHolder> {
    private final BrowseLocalContract.Presenter mPresenter;
    private Object[] mThumbnails;

    public BrowseAdapter(BrowseLocalContract.Presenter presenter) {
        super();
        mPresenter = presenter;
        mThumbnails = new Object[0];
    }

    @NonNull
    @Override
    public VideoElementHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_element, parent, false);
        return new VideoElementHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoElementHolder viewHolder, int position) {

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        VideoElement element = mPresenter.getItem(position);
        assert element != null;

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());
        viewHolder.icon.setTag(element.getName());
        viewHolder.subIcon.setTag(element.getName());
        // ToDo manage icons
        // ToDo manage long click
        if (element.isDirectory()) {
            viewHolder.subIcon.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.dossier).into(viewHolder.icon);
            //viewHolder.icon.setBackgroundResource(R.drawable.dossier);
            //mImageLoader.displayImage("drawable://" + R.drawable.dossier, viewHolder.icon);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.subIcon, mDirectoryOptions);
            setThumbnail(position, element, viewHolder.subIcon);
            viewHolder.itemView.setOnClickListener(v -> mPresenter.browseLocal(element));
        } else {
            viewHolder.subIcon.setVisibility(View.GONE);
            //element.setPosition(position, mHandler, mImageLoader, mFileOptions);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.icon, mFileOptions);
            setThumbnail(position, element, viewHolder.icon);
            //viewHolder.icon.setBackgroundResource(R.drawable.fichier);
            viewHolder.itemView.setOnClickListener(v -> mPresenter.playVideo(Uri.parse(element.getPath())));
        }
    }

    private void setThumbnail(int position, VideoElement element, ImageView view) {
        if (mThumbnails[position] != null) {
            if (mThumbnails[position] instanceof Uri) {
                Picasso.get().load((Uri) mThumbnails[position]).into(view);
            } else {
                Picasso.get().load((int) mThumbnails[position]).into(view);
            }
        } else {
            Picasso.get().load(R.drawable.loading_animation).into(view);
            mPresenter.getImageUri(element);
        }
    }

    @Override
    public int getItemCount() {
        return mPresenter.getCurrentItems().size();
    }

    public void updateThumbnail(VideoElement element, Uri uri) {
        int position = mPresenter.getCurrentItems().indexOf(element);
        if (position >= 0) {
            if (uri != null) {
                mThumbnails[position] = uri;
            } else {
                if (element.isDirectory()) {
                    mThumbnails[position] = R.drawable.empty;
                } else {
                    mThumbnails[position] = R.drawable.fichier;
                }
            }
            notifyItemChanged(position);
        }
    }

    class VideoElementHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.sub_icon)
        ImageView subIcon;

        private VideoElementHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void update() {
        mThumbnails = new Object[mPresenter.getCurrentItems().size()];
        notifyDataSetChanged();
    }
}
