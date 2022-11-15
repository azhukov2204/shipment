package ru.perekrestok.wms_native_mobile.screens.search_device.delegate

import ru.perekrestok.android.recycler_view.RecyclerViewItem

data class DeviceItem(
    override val id: Int,
    val name: String
) : RecyclerViewItem
