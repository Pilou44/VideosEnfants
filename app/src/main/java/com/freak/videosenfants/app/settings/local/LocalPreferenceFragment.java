package com.freak.videosenfants.app.settings.local;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.core.BaseFragment;
import com.freak.videosenfants.app.settings.SettingsContract;
import com.freak.videosenfants.elements.preferences.BrowsePreference;

import java.util.Vector;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;

public class LocalPreferenceFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LocalPreferenceFragment.class.getSimpleName();
    private static final boolean DEBUG = true;
    private Vector<BrowsePreference> mBrowsePrefs;
    private PreferenceScreen mScreen;

    @Inject
    public SettingsContract.Presenter mPresenter;

    @BindView(R.id.roots_list)
    RecyclerView mRootsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_local, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRootsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRootsList.setLayoutManager(layoutManager);
        mRootsList.setAdapter(new RootsListAdapter(mPresenter));

        return view;
    }

    /*@Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        int nbRoots = getResources().getInteger(R.integer.local_roots_number);

        for (int i = 0 ; i < nbRoots ; i++) {
            boolean visible = prefs.getBoolean(getString(R.string.key_local_browse) + "_" + i + getString(R.string.key_visible), false);
            boolean empty = prefs.getString(getString(R.string.key_local_browse) + "_" + i, "").length() == 0;

            if (DEBUG) {
                Log.i(TAG, "local_browse_" + i + " visible: " + visible);
                Log.i(TAG, "local_browse_" + i + " empty: " + empty);
            }

            if (!visible || empty) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.key_local_browse) + "_" + i + getString(R.string.key_visible), false);
                editor.remove(getString(R.string.key_local_browse) + "_" + i);
                editor.apply();
            }
        }

    }*/

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*if (key.startsWith(getString(R.string.key_local_browse)) && key.endsWith(getString(R.string.key_visible))){
            String indexString = key.substring(getString(R.string.key_local_browse).length() + 1 , key.lastIndexOf(getString(R.string.key_visible)));
            int index = Integer.parseInt(indexString);
            if (sharedPreferences.getBoolean(key, false)) {
                mScreen.addPreference(mBrowsePrefs.get(index));
            }
            else {
                mScreen.removePreference(mBrowsePrefs.get(index));
            }
        }*/
    }

    @OnClick(R.id.add_button)
    public void onAddButtonClicked() {
        BrowseLocalDialogFragment browseLocalDialogFragment = new BrowseLocalDialogFragment();
        browseLocalDialogFragment.setCancelable(true);
        browseLocalDialogFragment.show(getActivity().getSupportFragmentManager(), browseLocalDialogFragment.getTag());
    }

    public void refreshLocalRoots() {
        mRootsList.getAdapter().notifyDataSetChanged();
    }
}
