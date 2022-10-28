package ru.perekrestok.wms_native_mobile.screens.shipment

import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.android.view_model.event.UiEvent
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getCurrentShop
import ru.perekrestok.domain.entity.AppSetting.Companion.getCustomEndpoint
import ru.perekrestok.domain.entity.AppSetting.Companion.getDateInitApp
import ru.perekrestok.domain.entity.AppSetting.Companion.getHostnameSuffix
import ru.perekrestok.domain.entity.AppSetting.Companion.getIsCacheAllowed
import ru.perekrestok.domain.entity.AppSetting.Companion.getIsCookiesAllowed
import ru.perekrestok.domain.entity.AppSetting.Companion.getScanMode
import ru.perekrestok.domain.entity.AppSetting.Companion.getWebViewCacheMode
import ru.perekrestok.domain.entity.CustomSetting
import ru.perekrestok.domain.entity.DeviceConnectionState
import ru.perekrestok.domain.entity.ScanMode

data class ShipmentViewState(
    val appSettings: List<AppSetting<*>> = emptyList(),
    val customSettings: List<CustomSetting> = emptyList(),
    val isPageLoaded: Boolean = false,
    val loadingProgress: Int = 0,
    private val printerConnectionState: DeviceConnectionState = DeviceConnectionState.NO_DEVICE
) {

    companion object {
        private const val MAX_LOADING_PROGRESS = 100
    }

    private val shopBaseUrl = appSettings.getCustomEndpoint()?: appSettings.getCurrentShop()?.getShopUrl()

    val isDataReady: Boolean = shopBaseUrl?.isNotBlank() == true

    val webViewSettings: WebViewSettings = WebViewSettings(
        webViewCacheMode = appSettings.getWebViewCacheMode(),
        isCacheAllowed = appSettings.getIsCacheAllowed(),
        isCookiesAllowed = appSettings.getIsCookiesAllowed(),
        shopUrl = shopBaseUrl + appSettings.getHostnameSuffix()
    )

    val isProgressBarVisible: Boolean = loadingProgress < MAX_LOADING_PROGRESS
    val initData: String = appSettings.getDateInitApp().orEmpty()
    val isPrinterConnected: Boolean = printerConnectionState == DeviceConnectionState.CONNECTED
    val scanMode: ScanMode = appSettings.getScanMode()
}

data class WebViewSettings(
    val webViewCacheMode: Int,
    val isCacheAllowed: Boolean,
    val isCookiesAllowed: Boolean,
    val shopUrl: String
)

sealed interface ShipmentDataEvent : DataEvent {
    data class OnAppSettingsReceived(val appSettings: List<AppSetting<*>>) : ShipmentDataEvent
    data class OnCustomSettingsReceived(val customSettings: List<CustomSetting>) : ShipmentDataEvent
    data class OnPrinterConnectionStateReceived(val printerConnectionState: DeviceConnectionState) : ShipmentDataEvent
    data class OnBarcodeReceived(val barcode: String) : ShipmentDataEvent
}

sealed interface ShipmentUiEvent : UiEvent {
    data class OnPageLoadingProgressReceived(val progress: Int) : ShipmentUiEvent
    object ReloadPage : ShipmentUiEvent
    object OnPageLoaded : ShipmentUiEvent
    object ClearCookies: ShipmentUiEvent
    object ClearCache: ShipmentUiEvent
}

sealed interface ShipmentSingleEvent : SingleEvent {
    data class SetEnabledSwipeToRefresh(val isEnabled: Boolean) : ShipmentSingleEvent
    data class RenderJSCommand(val renderBarcodeJs: String, val isNeedPressEnter: Boolean) : ShipmentSingleEvent
    object ClearCache : ShipmentSingleEvent
    object ReloadData: ShipmentSingleEvent
}
