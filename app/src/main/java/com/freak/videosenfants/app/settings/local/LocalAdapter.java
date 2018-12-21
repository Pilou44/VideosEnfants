package com.freak.videosenfants.app.settings.local;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.freak.videosenfants.app.settings.SettingsContract;

class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.FileHolder> {

    private final SettingsContract.Presenter mPresenter;

    LocalAdapter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder fileHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mPresenter.getLocalFiles().size();
    }

    class FileHolder extends RecyclerView.ViewHolder {
        public FileHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
