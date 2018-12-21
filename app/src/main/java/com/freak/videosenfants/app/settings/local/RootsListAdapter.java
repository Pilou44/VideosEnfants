package com.freak.videosenfants.app.settings.local;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.settings.SettingsContract;
import com.freak.videosenfants.domain.bean.VideoElement;

import butterknife.BindView;
import butterknife.ButterKnife;

class RootsListAdapter extends RecyclerView.Adapter<RootsListAdapter.RootHolder> {
    private final SettingsContract.Presenter mPresenter;

    RootsListAdapter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @Override
    public RootHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings_root, parent, false);
        return new RootHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RootHolder viewHolder, int position) {
        VideoElement element = mPresenter.getLocalRoots().get(position);
        viewHolder.mSummary.setText(element.getPath());
        viewHolder.mRemoveButton.setOnClickListener(v -> mPresenter.removeLocalRoot(element));
    }

    @Override
    public int getItemCount() {
        return mPresenter.getLocalRoots().size();
    }

    class RootHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.summary)
        TextView mSummary;
        @BindView(R.id.value)
        TextView mValue;
        @BindView(R.id.remove_button)
        ImageButton mRemoveButton;
        private RootHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
