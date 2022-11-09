package ru.perekrestok.wms_native_mobile.screens.shops

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class ShopsScreen(private val afterChoseShoppRouting: ShopsFragment.Companion.AfterChoseShopRouting) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return ShopsFragment.newInstance(afterChoseShoppRouting)
    }
}
