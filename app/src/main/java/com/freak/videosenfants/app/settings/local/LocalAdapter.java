package com.freak.videosenfants.app.settings.local;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.settings.SettingsContract;
import com.freak.videosenfants.domain.bean.BrowsableElement;

import butterknife.BindView;
import butterknife.ButterKnife;

class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.FileHolder> {

    private final SettingsContract.Presenter mPresenter;
    private final int mType;
    private BrowsableElement mSelectedElement;

    LocalAdapter(SettingsContract.Presenter presenter, int type) {
        mPresenter = presenter;
        mType = type;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_element, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder fileHolder, int position) {
        BrowsableElement element = mPresenter.getFiles(mType).get(position);

        //il ne reste plus qu'à remplir notre vue
        fileHolder.mName.setText(element.getName());

        int padding = fileHolder.mLayout.getPaddingRight();
        fileHolder.mLayout.setPadding(
                padding * (element.getIndent() + 1),
                padding, padding, padding);

        if (element.equals(mSelectedElement)) {
            fileHolder.itemView.setBackgroundColor(0x66666666);
        } else {
            fileHolder.itemView.setBackgroundColor(0x00ffffff);
        }

        fileHolder.itemView.setOnClickListener(v -> {
            int oldSelected = mPresenter.getFiles(mType).indexOf(mSelectedElement);
            mSelectedElement = element;
            notifyItemChanged(oldSelected);
            if (!element.isExpanded()) {
                mPresenter.expand(element, mType);
            }
            fileHolder.itemView.setBackgroundColor(0x66666666);
        });
    }

    @Override
    public int getItemCount() {
        return mPresenter.getFiles(mType).size();
    }

    public BrowsableElement getSelected() {
        return mSelectedElement;
    }

    class FileHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        public TextView mName;
        @BindView(R.id.layout)
        public RelativeLayout mLayout;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
