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
import com.freak.videosenfants.elements.browsing.VideoElement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.VideoElementHolder> {
    private final BrowseLocalContract.Presenter mPresenter;

    public BrowseAdapter(BrowseLocalContract.Presenter presenter) {
        super();
        mPresenter = presenter;
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
            viewHolder.icon.setBackgroundResource(R.drawable.dossier);
            //mImageLoader.displayImage("drawable://" + R.drawable.dossier, viewHolder.icon);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.subIcon, mDirectoryOptions);
            viewHolder.itemView.setOnClickListener(v -> mPresenter.browseLocal(element));
        } else {
            viewHolder.subIcon.setVisibility(View.GONE);
            //element.setPosition(position, mHandler, mImageLoader, mFileOptions);
            //mImageLoader.displayImage(element.getImageURI(), viewHolder.icon, mFileOptions);
            viewHolder.icon.setBackgroundResource(R.drawable.fichier);
            viewHolder.itemView.setOnClickListener(v -> mPresenter.playVideo(Uri.parse(element.getPath())));
        }
    }

    @Override
    public int getItemCount() {
        return mPresenter.getCurrentItems().size();
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
}
