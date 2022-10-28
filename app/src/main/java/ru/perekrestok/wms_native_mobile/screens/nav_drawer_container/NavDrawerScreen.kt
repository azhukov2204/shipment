package ru.perekrestok.wms_native_mobile.screens.nav_drawer_container

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

object NavDrawerScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return NavDrawerFragment.newInstance()
    }
}