package ru.perekrestok.wms_native_mobile.screens.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

object SettingsScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SettingsFragment.newInstance()
    }
}
