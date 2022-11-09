package ru.perekrestok.wms_native_mobile.screens.shops

import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.android.view_model.event.UiEvent
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.kotlin.StringPatterns
import ru.perekrestok.wms_native_mobile.screens.shops.delegate.ShopItem

data class ShopsViewState(
    val shops: List<Shop> = emptyList(),
    val currentShop: Shop? = null,
    private val searchQuery: String = StringPatterns.EMPTY_SYMBOL
) {

    val shopItems: List<ShopItem> =
        shops
            .toAdapterItems()
            .filter { shop ->
                if (searchQuery.isEmpty()) {
                    true
                } else {
                    shop.title.lowercase().contains(searchQuery.lowercase())
                }
            }

    val isAnimationLoading: Boolean = shops.isEmpty()

    val isCurrentShopNotEmpty: Boolean = currentShop != null
}

sealed interface ShopsDataEvent : DataEvent {
    data class OnShopsReceived(val shops: List<Shop>) : ShopsDataEvent
    data class OnChosenShopReceived(val shop: Shop) : ShopsDataEvent
}

sealed interface ShopsUiEvent : UiEvent {
    data class OnShopClicked(val shopId: Int) : ShopsUiEvent
    data class OnSearchQueryFilled(val searchQuery: String) : ShopsUiEvent
    object RefreshShopList : ShopsUiEvent
    object OnCloseButtonClicked : ShopsUiEvent
    object OnImeSearchClicked : ShopsUiEvent
}

sealed interface ShopsSingleEvent : SingleEvent {
    object ClearSearchFieldFocus : ShopsSingleEvent
}

private fun List<Shop>.toAdapterItems(): List<ShopItem> {
    return map { shop: Shop ->
        ShopItem(
            name = shop.name,
            sapId = shop.sapId,
            id = shop.id,
            shopKpp = shop.kpp,
            hostName = shop.hostName,
            mapPosition = shop.mapPosition
        )
    }
}
