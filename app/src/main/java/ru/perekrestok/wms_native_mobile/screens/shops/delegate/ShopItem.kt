package ru.perekrestok.wms_native_mobile.screens.shops.delegate

import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.domain.entity.MapPosition

data class ShopItem(
    override val id: Int,
    val name: String,
    val sapId: String,
    val shopKpp: String,
    val hostName: String,
    val mapPosition: MapPosition
) : RecyclerViewItem {

    val title = "$sapId $name"

    override fun areItemsTheSame(comparableItem: RecyclerViewItem): Boolean {
        return id == (comparableItem as? ShopItem)?.id
    }
}
