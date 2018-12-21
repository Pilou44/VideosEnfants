package com.freak.videosenfants.app.settings

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import com.freak.videosenfants.R
import com.freak.videosenfants.app.core.BaseFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MainSettingsFragment : BaseFragment() {

    @Inject
    lateinit var mPresenter: SettingsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_main, container, false)
        mUnbinder = ButterKnife.bind(this, view)

        return view
    }

    @OnClick(R.id.local_settings)
    fun onLocalClicked() {
        mPresenter.showLocalSettings(id);
    }

    @OnClick(R.id.dlna_settings)
    fun onDlnaClicked() {
        mPresenter.showDlnaSettings(id);
    }

    @OnClick(R.id.general_settings)
    fun onGeneralClicked() {
        mPresenter.showGeneralSettings(id);
    }

    @OnClick(R.id.memory_settings)
    fun onMemoryClicked() {
        mPresenter.showMemorySettings(id);
    }
}
