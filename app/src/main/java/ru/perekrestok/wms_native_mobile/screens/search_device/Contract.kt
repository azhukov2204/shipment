package ru.perekrestok.wms_native_mobile.screens.search_device

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.UiEvent
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedPrinter
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedScanner
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.kotlin.StringPatterns
import ru.perekrestok.kotlin.addToList
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.screens.search_device.delegate.DeviceItem

data class SearchDeviceViewState(
    val deviceType: DeviceType,
    private val stringResourceProvider: StringResourceProvider,
    val devices: Set<Device> = emptySet(),
    val errorMessage: String = StringPatterns.EMPTY_SYMBOL,
    private val settings: List<AppSetting<*>> = emptyList()
) {
    val hasError: Boolean = errorMessage.isNotEmpty()
    val isAnimationLoading: Boolean = !hasError

    private val currentPrinter: Device? = settings.getSelectedPrinter()
    private val currentScanner: Device? = settings.getSelectedScanner()
    private val currentScannerName = currentScanner?.name
        ?: stringResourceProvider.getStringResource(R.string.text_not_selected)
    private val currentPrinterName = currentPrinter?.name
        ?: stringResourceProvider.getStringResource(R.string.text_not_selected)

    val deviceItems: List<RecyclerViewItem> = buildList {
        DeviceItem(
            id = -1,
            name = stringResourceProvider.getStringResource(R.string.text_not_selected),
        ).addToList(this)
        devices.toList().map { it.toAdapterItem() }.addToList(this)
    }

    val currentDeviceText: String = when (deviceType) {
        DeviceType.PRINTER ->
            stringResourceProvider.getStringResource(R.string.text_current_printer, currentPrinterName)

        DeviceType.SCANNER ->
            stringResourceProvider.getStringResource(R.string.text_current_scanner, currentScannerName)
    }
}

sealed interface SearchDeviceDataEvent : DataEvent {
    data class OnDevicesReceived(val devices: Set<Device>) : SearchDeviceDataEvent
    data class OnErrorHappened(val errorMessage: String) : SearchDeviceDataEvent
    data class OnAppSettingsReceived(val settings: List<AppSetting<*>>) : SearchDeviceDataEvent
}

sealed interface SearchDeviceUiEvent : UiEvent {
    object OnCloseButtonClicked : SearchDeviceUiEvent
    data class OnDeviceItemClicked(val deviceHashCode: Int) : SearchDeviceUiEvent
}

@Parcelize
enum class DeviceType : Parcelable {
    PRINTER,
    SCANNER
}

private fun Device.toAdapterItem(): DeviceItem {
    return DeviceItem(
        id = hashCode(),
        name = toString()
    )
}
