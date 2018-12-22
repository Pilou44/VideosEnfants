package com.freak.videosenfants.app.settings.local;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.core.BaseDialogFragment;
import com.freak.videosenfants.app.settings.SettingsContract;
import com.freak.videosenfants.domain.bean.BrowsableElement;
import com.freak.videosenfants.domain.bean.FileElement;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;

public class BrowseLocalDialogFragment extends BaseDialogFragment {

    @Inject
    public SettingsContract.Presenter mPresenter;

    @BindView(R.id.tree)
    RecyclerView mTree;

    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_browse, container);
        mUnbinder = ButterKnife.bind(this, view);

        mPresenter.retrieveSources(mType);

        mTree.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mTree.setLayoutManager(layoutManager);
        mTree.setAdapter(new LocalAdapter(mPresenter, mType));

        return view;
    }

    public void refreshLocalSources() {
        mTree.getAdapter().notifyDataSetChanged();
    }

    public void notifyLocalSubsRetrieved(int position, int size) {
        mTree.getAdapter().notifyItemChanged(position - 1);
        mTree.getAdapter().notifyItemRangeInserted(position, size);
    }

    @OnClick(R.id.ok_button)
    public void onOkButtonPressed() {
        mPresenter.addRoot((BrowsableElement) ((LocalAdapter) mTree.getAdapter()).getSelected(), mType);
        dismiss();
    }

    public void setType(int type) {
        mType = type;
    }
}
