package com.freak.videosenfants.app.settings

import android.support.v4.app.Fragment
import com.freak.videosenfants.app.core.BaseRouter
import com.freak.videosenfants.app.settings.dlna.DlnaPreferenceFragment
import com.freak.videosenfants.app.settings.local.LocalPreferenceFragment

class SettingsRouter : BaseRouter(), SettingsContract.Router {

    override fun showMainSettings(view: SettingsContract.View?, fragmentId: Int) {
        changeFragment(view!!, MainSettingsFragment(), fragmentId)
    }

    override fun showDlnaSettings(view: SettingsContract.View?, fragmentId: Int) {
        changeFragment(view!!, DlnaPreferenceFragment(), fragmentId)
    }

    override fun showGeneralSettings(view: SettingsContract.View?, fragmentId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMemorySettings(view: SettingsContract.View?, fragmentId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLocalSettings(view: SettingsContract.View?, fragmentId: Int) {
        changeFragment(view!!, LocalPreferenceFragment(), fragmentId)
    }

    private fun changeFragment(view: SettingsContract.View, fragment: Fragment, fragmentId: Int) {
        val ft = getActivity(view).getSupportFragmentManager().beginTransaction()
        ft.replace(fragmentId, fragment).addToBackStack(null).commit()
    }

}
