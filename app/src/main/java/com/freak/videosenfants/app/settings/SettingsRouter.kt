package com.freak.videosenfants.app.settings

import android.support.v4.app.Fragment
import com.freak.videosenfants.app.core.BaseRouter
import com.freak.videosenfants.app.settings.local.LocalPreferenceFragment
import android.os.Bundle



class SettingsRouter : BaseRouter(), SettingsContract.Router {

    override fun showMainSettings(view: SettingsContract.View?, fragmentId: Int) {
        val ft = getActivity(view).getSupportFragmentManager().beginTransaction()
        ft.replace(fragmentId, MainSettingsFragment()).commit()
    }

    override fun showDlnaSettings(view: SettingsContract.View?, fragmentId: Int) {
        val bundle = Bundle()
        bundle.putString(LocalPreferenceFragment.KEY_TYPE, LocalPreferenceFragment.TYPE_UPNP)
        val fragment = LocalPreferenceFragment();
        fragment.arguments = bundle;
        changeFragment(view!!, fragment, fragmentId)
    }

    override fun showGeneralSettings(view: SettingsContract.View?, fragmentId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMemorySettings(view: SettingsContract.View?, fragmentId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLocalSettings(view: SettingsContract.View?, fragmentId: Int) {
        val bundle = Bundle()
        bundle.putString(LocalPreferenceFragment.KEY_TYPE, LocalPreferenceFragment.TYPE_LOCAL)
        val fragment = LocalPreferenceFragment();
        fragment.arguments = bundle;
        changeFragment(view!!, fragment, fragmentId)
    }

    private fun changeFragment(view: SettingsContract.View, fragment: Fragment, fragmentId: Int) {
        val ft = getActivity(view).getSupportFragmentManager().beginTransaction()
        ft.replace(fragmentId, fragment).addToBackStack(null).commit()
    }
}
