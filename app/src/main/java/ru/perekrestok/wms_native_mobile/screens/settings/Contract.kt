package ru.perekrestok.wms_native_mobile.screens.settings

import android.content.pm.ActivityInfo
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItem
import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.UiEvent
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getCurrentShop
import ru.perekrestok.domain.entity.AppSetting.Companion.getCustomEndpoint
import ru.perekrestok.domain.entity.AppSetting.Companion.getIsAdminMode
import ru.perekrestok.domain.entity.AppSetting.Companion.getIsCacheAllowed
import ru.perekrestok.domain.entity.AppSetting.Companion.getIsCookiesAllowed
import ru.perekrestok.domain.entity.AppSetting.Companion.getScanMode
import ru.perekrestok.domain.entity.AppSetting.Companion.getScreenOrientation
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedPrinter
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedScanner
import ru.perekrestok.domain.entity.AppSetting.Companion.getWebViewCacheMode
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.screens.WEB_VIEW_CACHE_MODES

data class SettingsViewState(
    private val stringResourceProvider: StringResourceProvider,
    val appSettings: List<AppSetting<*>> = emptyList()
) {
    private val currentShop: Shop? = appSettings.getCurrentShop()
    private val screenOrientation: Int = appSettings.getScreenOrientation()
    private val webViewCacheMode: Int = appSettings.getWebViewCacheMode()
    val isAdminModeEnabled: Boolean = appSettings.getIsAdminMode()
    val isCookiesAllowed: Boolean = appSettings.getIsCookiesAllowed()
    val isCacheAllowed: Boolean = appSettings.getIsCacheAllowed()
    private val currentPrinter: Device? = appSettings.getSelectedPrinter()
    private val currentScanner: Device? = appSettings.getSelectedScanner()
    val customUrl: String = appSettings.getCustomEndpoint().orEmpty()
    private val scanMode: ScanMode = appSettings.getScanMode()

    val scanModeDescription: String = getScanModeDescription(scanMode)

    val shopName: String = currentShop?.name
        ?: stringResourceProvider.getStringResource(R.string.text_not_selected)

    val printerName: String = currentPrinter?.name
        ?: stringResourceProvider.getStringResource(R.string.text_not_selected)

    val scannerName: String = currentScanner?.name
        ?: stringResourceProvider.getStringResource(R.string.text_not_selected)

    val orientationItems: List<ListDialogItem> = SCREEN_ORIENTATIONS.map { orientationItem ->
        ListDialogItem(
            id = orientationItem.key,
            name = stringResourceProvider.getStringResource(orientationItem.value),
            isSelected = orientationItem.key == screenOrientation
        )
    }

    val screenOrientationName: String = (SCREEN_ORIENTATIONS[screenOrientation] ?: R.string.text_horizontal_orientation)
        .let { stringResId ->
            stringResourceProvider.getStringResource(stringResId)
        }

    val cachedModeItems: List<ListDialogItem> = WEB_VIEW_CACHE_MODES.map { cachedModeItem ->
        ListDialogItem(
            id = cachedModeItem.id,
            name = cachedModeItem.displayName,
            isSelected = cachedModeItem.id == webViewCacheMode
        )
    }

    val cachedModeName: String = WEB_VIEW_CACHE_MODES.find { it.id == webViewCacheMode }?.displayName
        ?: WEB_VIEW_CACHE_MODE_DEFAULT_NAME

    val scanModeItems: List<ListDialogItem> = ScanMode.values().map { scanModeItem ->
        ListDialogItem(
            id = scanModeItem.id,
            name = getScanModeDescription(scanModeItem),
            isSelected = scanModeItem == scanMode
        )
    }

    private fun getScanModeDescription(scanMode: ScanMode): String {
        return when (scanMode) {
            ScanMode.TO_ACTIVE_FIELD -> R.string.text_scan_mode_to_active_field
            ScanMode.RUN_JS -> R.string.text_scan_mode_js
        }.let(stringResourceProvider::getStringResource)
    }

    companion object {
        private val SCREEN_ORIENTATIONS = mapOf(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE to R.string.text_horizontal_orientation,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT to R.string.text_vertical_orientation
        )

        private const val WEB_VIEW_CACHE_MODE_DEFAULT_NAME = "DEFAULT"
    }
}

sealed interface SettingsDataEvent : DataEvent {
    data class OnSettingsReceived(val appSettings: List<AppSetting<*>>) : SettingsDataEvent
}

sealed interface SettingsUiEvent : UiEvent {
    object OnAdminModeItemClicked : SettingsUiEvent
    object OnSelectShopItemClicked : SettingsUiEvent
    object OnSelectPrinterItemClicked : SettingsUiEvent
    object OnSelectScannerItemClicked : SettingsUiEvent
    object OnScanModeItemClicked : SettingsUiEvent
    object OnSelectOrientationItemClicked : SettingsUiEvent
    object OnCookiesAllowedItemClicked : SettingsUiEvent
    object OnCacheAllowedItemClicked : SettingsUiEvent
    object OnCacheModeItemClicked : SettingsUiEvent
    object OnClearCacheItemClicked : SettingsUiEvent
    object OnClearCookiesItemClicked : SettingsUiEvent
    object OnCustomEndpointItemClicked : SettingsUiEvent
}
