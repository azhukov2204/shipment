package ru.perekrestok.wms_native_mobile.screens.search_device

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class SearchDeviceScreen(private val deviceType: DeviceType) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SearchDeviceFragment.newInstance(deviceType)
    }
}
