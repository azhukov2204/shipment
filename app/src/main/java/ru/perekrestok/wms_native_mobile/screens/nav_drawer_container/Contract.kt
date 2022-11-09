package ru.perekrestok.wms_native_mobile.screens.nav_drawer_container

import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.android.view_model.event.UiEvent
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getCurrentShop
import ru.perekrestok.domain.entity.AppSetting.Companion.getDateInitApp
import ru.perekrestok.domain.entity.DeviceConnectionState
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R

data class NavDrawerViewState(
    private val stringResourceProvider: StringResourceProvider,
    val appSettings: List<AppSetting<*>> = emptyList(),
    val isAlreadyInitialized: Boolean = false,
    val scannerConnectionState: DeviceConnectionState = DeviceConnectionState.NO_DEVICE,
    val printerConnectionState: DeviceConnectionState = DeviceConnectionState.NO_DEVICE,
    val toolbarTitle: String = stringResourceProvider.getStringResource(R.string.text_scanning),
    val isToolbarVisible: Boolean = false

) {
    private val currentShop: Shop? = appSettings.getCurrentShop()
    val initDate: String = appSettings.getDateInitApp().orEmpty()

    val shopName: String = currentShop?.name.orEmpty()
    val shopHost: String = currentShop?.hostName.orEmpty()

    @ColorRes
    val scannerIconColorRes: Int = when (scannerConnectionState) {
        DeviceConnectionState.NO_DEVICE -> R.attr.colorStateNoDevice
        DeviceConnectionState.DISCONNECTED -> R.attr.colorStateDisconnected
        DeviceConnectionState.CONNECTING -> R.attr.colorStateConnecting
        DeviceConnectionState.CONNECTED -> R.attr.colorStateConnected
    }

    @ColorRes
    val printerIconColorRes: Int = when (printerConnectionState) {
        DeviceConnectionState.NO_DEVICE -> R.attr.colorStateNoDevice
        DeviceConnectionState.DISCONNECTED -> R.attr.colorStateDisconnected
        DeviceConnectionState.CONNECTING -> R.attr.colorStateConnecting
        DeviceConnectionState.CONNECTED -> R.attr.colorStateConnected
    }
}

sealed interface NavDrawerUiEvent : UiEvent {
    data class OnMenuItemClicked(@IdRes val menuItemId: Int) : NavDrawerUiEvent
    object OnMenuButtonClicked : NavDrawerUiEvent
    object OnAboutButtonClicked : NavDrawerUiEvent
}

sealed interface NavDrawerDataEvent : DataEvent {
    data class OnScannerConnectionStateReceived(val deviceConnectionState: DeviceConnectionState) :
        NavDrawerDataEvent

    data class OnPrinterConnectionStateReceived(val deviceConnectionState: DeviceConnectionState) :
        NavDrawerDataEvent

    data class OnSettingsReceived(val settings: List<AppSetting<*>>) : NavDrawerDataEvent
}

sealed interface NavDrawerSingleEvent : SingleEvent {
    object OpenNavigationDrawer : NavDrawerSingleEvent
}
