package ru.perekrestok.wms_native_mobile.screens.shipment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

object ShipmentScreen : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return ShipmentFragment.newInstance()
    }
}
