package ru.perekrestok.wms_native_mobile.screens.shipment.web_view

import android.widget.Toast
import kotlinx.coroutines.launch
import ru.perekrestok.android.dialogs.message.MessageDialogScreen
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.entity.AppSetting.Companion.getSettingValueByKey
import ru.perekrestok.domain.entity.CustomSetting.Companion.getValueByKey
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.exception.PrinterException
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.interactor.WebViewOptionInteractor
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.screens.WEB_VIEW_CACHE_MODES
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerRouter
import ru.perekrestok.wms_native_mobile.screens.shipment.ShipmentScreen
import ru.perekrestok.wms_native_mobile.screens.shipment.ShipmentSingleEvent
import ru.perekrestok.wms_native_mobile.screens.shipment.ShipmentViewState
import timber.log.Timber

@Suppress("LongParameterList", "TooManyFunctions")
abstract class ShipmentJSViewModel(
    private val systemActionInteractor: SystemActionInteractor,
    private val navDrawerRouter: NavDrawerRouter,
    private val settingsInteractor: SettingsInteractor,
    private val deviceInteractor: DeviceInteractor,
    private val stringResourceProvider: StringResourceProvider,
    private val webViewOptionInteractor: WebViewOptionInteractor
) : BaseViewModel<ShipmentViewState>(), ShipmentJS {

    companion object {
        private const val GET_APP_CONFIG_VALUE_ERROR_MESSAGE = "getAppConfigValue: key is null"
        private const val SET_APP_CONFIG_VALUE_ERROR_MESSAGE = "setAppConfigValue: key or value is null"
        private const val SET_NATIVE_CONFIG_VALUE_ERROR_MESSAGE = "setNativeConfigValue: key or type or value is null"
        private const val GET_NATIVE_CONFIG_VALUE_ERROR_MESSAGE = "getNativeConfigValue: key is null"
    }

    private var currentState: ShipmentViewState = ShipmentViewState()

    override suspend fun onAfterStateChanged(
        oldViewState: ShipmentViewState,
        newViewState: ShipmentViewState,
        event: Event
    ) {
        currentState = newViewState
    }

    @android.webkit.JavascriptInterface
    override fun getAppVersion(): String {
        return systemActionInteractor.getAppName()
    }

    @android.webkit.JavascriptInterface
    override fun getAppCode(): Long {
        return systemActionInteractor.getAppCode()
    }

    @android.webkit.JavascriptInterface
    override fun getDateInitApp(): String {
        return currentState.initData
    }

    @android.webkit.JavascriptInterface
    override fun getDateInstallApp(): String {
        return systemActionInteractor.getDateInstallApp()
    }

    @android.webkit.JavascriptInterface
    override fun getImei(): String {
        return systemActionInteractor.getImei()
    }

    @android.webkit.JavascriptInterface
    override fun getNativeConfigValue(key: String?, type: String?): String? {
        return if (key == null) {
            navDrawerRouter.showErrorSnackbar(errorText = GET_NATIVE_CONFIG_VALUE_ERROR_MESSAGE)
            null
        } else {
            currentState.customSettings.getValueByKey(key)
        }
    }

    @android.webkit.JavascriptInterface
    override fun getAppConfigValue(key: String?, type: String?): String? {
        return if (key != null) {
            currentState.appSettings.getSettingValueByKey(key)
        } else {
            navDrawerRouter.showErrorSnackbar(errorText = GET_APP_CONFIG_VALUE_ERROR_MESSAGE)
            null
        }
    }

    @android.webkit.JavascriptInterface
    override fun setNativeConfigValue(key: String?, type: String?, value: String?): Boolean {
        return if (key != null && type != null && value != null) {
            viewModelScopeIO.launch {
                settingsInteractor.saveCustomSetting(key, type, value)
            }
            true
        } else {
            navDrawerRouter.showErrorSnackbar(errorText = SET_NATIVE_CONFIG_VALUE_ERROR_MESSAGE)
            false
        }
    }

    @android.webkit.JavascriptInterface
    override fun setAppConfigValue(key: String?, type: String?, value: String?): Boolean {
        return if (key != null && value != null) {
            viewModelScopeIO.launch {
                settingsInteractor.setSettingValueByKey(key, value)
            }
            true
        } else {
            navDrawerRouter.showErrorSnackbar(errorText = SET_APP_CONFIG_VALUE_ERROR_MESSAGE)
            false
        }
    }

    @android.webkit.JavascriptInterface
    override fun isPrinterConnected(): Boolean {
        return currentState.isPrinterConnected
    }

    @android.webkit.JavascriptInterface
    override fun connectToPrinter() {
        viewModelScopeIO.launch {
            deviceInteractor.connectToPrinter().onFailure { error -> handleError(error) }
        }
    }

    @android.webkit.JavascriptInterface
    override fun sendDataToPrinter(data: String?, encoding: String?) {
        if (data.isNullOrBlank()) {
            navDrawerRouter.showErrorSnackbar(
                errorText = stringResourceProvider
                    .getStringResource(R.string.text_error_bluetooth_printer_data_is_empty)
            )
        } else {
            viewModelScopeIO.launch {
                deviceInteractor.printData(data, encoding).onFailure { error ->
                    handleError(error)
                }
            }
        }
    }

    @android.webkit.JavascriptInterface
    override fun disconnectFromPrinter() {
        viewModelScopeIO.launch {
            deviceInteractor.disconnectFromPrinter().onFailure { error ->
                handleError(error)
            }
        }
    }

    @android.webkit.JavascriptInterface
    override fun sendDataLonelyToPrinter(data: String?, encoding: String?) {
        if (data.isNullOrBlank()) {
            navDrawerRouter.showErrorSnackbar(
                errorText = stringResourceProvider
                    .getStringResource(R.string.text_error_bluetooth_printer_data_is_empty)
            )
        } else {
            viewModelScopeIO.launch {
                deviceInteractor.printDataLonely(data, encoding).onFailure { error ->
                    handleError(error)
                }
            }
        }
    }

    @android.webkit.JavascriptInterface
    override fun clearCache() {
        processSingleEvent(ShipmentSingleEvent.ClearCache)
    }

    @android.webkit.JavascriptInterface
    override fun clearCookies() {
        viewModelScopeIO.launch {
            systemActionInteractor.clearCookies()
        }
    }

    @android.webkit.JavascriptInterface
    override fun setCacheMode(mode: String) {
        WEB_VIEW_CACHE_MODES.find { it.modeName.equals(mode, ignoreCase = true) }?.let { cacheMode ->
            viewModelScopeIO.launch { webViewOptionInteractor.saveCacheMode(cacheMode.id) }
            restartWebView()
        }
    }

    @Deprecated("Логика внутреннего сканера больше не используется")
    @android.webkit.JavascriptInterface
    override fun setInternalScannerEnabled(isEnabled: Boolean) {
        viewModelScopeIO.launch {
            deviceInteractor.setIsInternalScannerEnabled(isEnabled)
        }
    }

    @android.webkit.JavascriptInterface
    override fun startScanByPhoto(closeAfterScan: Boolean) {
        // nothing
    }

    @android.webkit.JavascriptInterface
    override fun showDialog(message: String, buttonText: String) {
        navDrawerRouter.addTo(MessageDialogScreen(message = message, buttonText = buttonText))
    }

    @android.webkit.JavascriptInterface
    override fun showSnackbar(message: String) {
        navDrawerRouter.showSnackBar(text = message)
    }

    @android.webkit.JavascriptInterface
    override fun showToast(message: String) {
        navDrawerRouter.showToast(message, Toast.LENGTH_SHORT)
    }

    @android.webkit.JavascriptInterface
    override fun showToastLong(message: String) {
        navDrawerRouter.showToast(message, Toast.LENGTH_LONG)
    }

    @android.webkit.JavascriptInterface
    override fun showError(message: String, details: String) {
        navDrawerRouter.showErrorSnackbar(message, details)
    }

    @android.webkit.JavascriptInterface
    override fun doVibro(time: Long) {
        systemActionInteractor.doVibro(time)
    }

    @android.webkit.JavascriptInterface
    override fun restartWebView() {
        navDrawerRouter.replaceScreen(ShipmentScreen)
    }

    @android.webkit.JavascriptInterface
    override fun restartApp() {
        systemActionInteractor.restartApp()
    }

    @android.webkit.JavascriptInterface
    override fun setEnabledSwipeToRefresh(isEnabled: Boolean) {
        processSingleEvent(ShipmentSingleEvent.SetEnabledSwipeToRefresh(isEnabled))
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is BluetoothDeviceException -> handleBluetoothDeviceException(error)
            is PrinterException -> handlePrinterException(error)
            else -> handleBasicException(error)
        }
    }

    private fun handleBluetoothDeviceException(error: BluetoothDeviceException) {
        val errorMessageResourceId = when (error) {
            BluetoothDeviceException.FailedToEnable -> R.string.text_error_enable_bluetooth
        }
        navDrawerRouter.showErrorSnackbar(
            errorText = stringResourceProvider.getStringResource(errorMessageResourceId)
        )
    }

    private fun handlePrinterException(error: PrinterException) {
        when (error) {
            is PrinterException.CouldNotOpenConnection -> {
                navDrawerRouter.showErrorSnackbar(
                    errorText = stringResourceProvider
                        .getStringResource(R.string.text_error_on_open_printer_connection),
                    errorDetails = error.message
                )
            }
            PrinterException.NoPrinter -> {
                navDrawerRouter.showErrorSnackbar(
                    errorText = stringResourceProvider
                        .getStringResource(R.string.text_error_bluetooth_printer_not_selected),
                    errorDetails = stringResourceProvider
                        .getStringResource(R.string.text_error_bluetooth_printer_not_selected_details)
                )
            }
        }
    }

    private fun handleBasicException(error: Throwable) {
        Timber.e(error)
        navDrawerRouter.showErrorSnackbar(
            errorText = error.message.orEmpty(),
            errorDetails = error.cause?.message.orEmpty()
        )
    }
}
