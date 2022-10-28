package ru.perekrestok.wms_native_mobile.screens.splash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

object SplashScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SplashFragment.newInstance()
    }
}
