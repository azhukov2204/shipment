package ru.perekrestok.wms_native_mobile.screens.settings

import android.text.InputType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.perekrestok.android.dialogs.alert.AlertDialogScreen
import ru.perekrestok.android.dialogs.edit_text.EditTextDialogScreen
import ru.perekrestok.android.dialogs.list.ListDialogScreen
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItem
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItems
import ru.perekrestok.android.dialogs.waiting.WaitingDialogScreen
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.exception.AdminModeException
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.interactor.AdminModeInteractor
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.ScreenOrientationInteractor
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.interactor.WebViewOptionInteractor
import ru.perekrestok.domain.interactor.WireCommand
import ru.perekrestok.domain.interactor.WireInteractor
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.activity.MainActivityRouter
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerRouter
import ru.perekrestok.wms_native_mobile.screens.shops.ShopsFragment
import ru.perekrestok.wms_native_mobile.screens.shops.ShopsScreen
import timber.log.Timber

@Suppress("LongParameterList", "TooManyFunctions")
class SettingsViewModel(
    private val navDrawerRouter: NavDrawerRouter,
    private val mainActivityRouter: MainActivityRouter,
    private val stringResourceProvider: StringResourceProvider,
    private val adminModeInteractor: AdminModeInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val deviceInteractor: DeviceInteractor,
    private val screenOrientationInteractor: ScreenOrientationInteractor,
    private val webViewOptionInteractor: WebViewOptionInteractor,
    private val wireInteractor: WireInteractor,
    private val systemActionInteractor: SystemActionInteractor
) : BaseViewModel<SettingsViewState>() {

    init {
        viewModelScopeIO.launch {
            settingsInteractor.getAppSettingsFlow().collect { settings ->
                processDataEvent(SettingsDataEvent.OnSettingsReceived(settings))
            }
        }
    }

    private var currentJob: Job? = null

    override fun initialViewState(): SettingsViewState =
        SettingsViewState(stringResourceProvider = stringResourceProvider)

    override suspend fun reduce(event: Event, currentState: SettingsViewState): SettingsViewState = when (event) {
        is SettingsUiEvent -> dispatchSettingsUiEvent(event, currentState)
        is SettingsDataEvent -> dispatchSettingsDataEvent(event, currentState)
        else -> currentState
    }

    private fun dispatchSettingsDataEvent(
        event: SettingsDataEvent,
        currentState: SettingsViewState
    ): SettingsViewState = when (event) {
        is SettingsDataEvent.OnSettingsReceived -> currentState.copy(appSettings = event.appSettings)
        is SettingsDataEvent.OnPrinterDevicesReceived -> currentState.copy(printerDevices = event.printerDevices)
            .also { handleOnPrinterDevicesReceived(event, currentState) }
        is SettingsDataEvent.OnScannerDevicesReceived -> currentState.copy(scannerDevices = event.scannerDevices)
            .also { handleOnScannerDevicesReceived(event, currentState) }
    }

    @Suppress("ComplexMethod")
    private fun dispatchSettingsUiEvent(event: SettingsUiEvent, currentState: SettingsViewState): SettingsViewState =
        when (event) {
            SettingsUiEvent.OnAdminModeItemClicked -> currentState.also {
                handleOnAdminModeItemClicked(currentState)
            }
            SettingsUiEvent.OnSelectShopItemClicked -> currentState.also {
                handleOnSelectShopItemClicked()
            }
            SettingsUiEvent.OnSelectPrinterItemClicked -> currentState.also {
                handleOnSelectPrinterItemClicked()
            }
            SettingsUiEvent.OnSelectScannerItemClicked -> currentState.also {
                handleOnSelectScannerItemClicked()
            }
            SettingsUiEvent.OnScanModeItemClicked -> currentState.also {
                handleOnScanModeItemClicked(currentState)
            }
            SettingsUiEvent.OnSelectOrientationItemClicked -> currentState.also {
                handleOnSelectOrientationItemClicked(currentState)
            }
            SettingsUiEvent.OnCookiesAllowedItemClicked -> currentState.also {
                handleOnCookiesAllowedItemClicked(currentState)
            }
            SettingsUiEvent.OnCacheAllowedItemClicked -> currentState.also {
                handleOnCacheAllowedItemClicked(currentState)
            }
            SettingsUiEvent.OnCacheModeItemClicked -> currentState.also {
                handleOnCacheModeItemClicked(currentState)
            }
            SettingsUiEvent.OnClearCacheItemClicked -> currentState.also {
                handleOnClearCacheItemClicked()
            }
            SettingsUiEvent.OnClearCookiesItemClicked -> currentState.also {
                handleOnClearCookiesItemClicked()
            }
            SettingsUiEvent.OnCustomEndpointItemClicked -> currentState.also {
                handleOnCustomEndpointItemClicked(currentState)
            }
        }

    private fun handleOnClearCacheItemClicked() {
        navDrawerRouter.addTo(
            AlertDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_attention),
                message = stringResourceProvider.getStringResource(R.string.text_clear_cache_confirmation_message)
            ) {
                viewModelScopeIO.launch { wireInteractor.sendCommand(WireCommand.ClearCache) }
            }
        )
    }

    private fun handleOnClearCookiesItemClicked() {
        navDrawerRouter.addTo(
            AlertDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_attention),
                message = stringResourceProvider.getStringResource(R.string.text_clear_cookies_confirmation_message)
            ) {
                viewModelScopeIO.launch { systemActionInteractor.clearCookies() }
            }
        )
    }

    private fun handleOnCustomEndpointItemClicked(currentState: SettingsViewState) {
        navDrawerRouter.addTo(
            EditTextDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_custom_endpoint_title),
                text = currentState.customUrl,
                inputType = InputType.TYPE_CLASS_TEXT,
                positiveButtonText = stringResourceProvider.getStringResource(R.string.text_button_apply),
                negativeButtonText = stringResourceProvider.getStringResource(R.string.text_button_cancel),
                neutralButtonText = stringResourceProvider.getStringResource(R.string.text_button_clear),
                positiveButtonAction = { url -> saveCustomEndpoint(url) },
                neutralButtonAction = { clearCustomEndpoint() }
            )
        )
    }

    private fun saveCustomEndpoint(url: String) = viewModelScopeIO.launch {
        webViewOptionInteractor.saveCustomEndpoint(url)
    }

    private fun clearCustomEndpoint() = viewModelScopeIO.launch {
        webViewOptionInteractor.removeCustomEndpoint()
    }

    private fun handleOnAdminModeItemClicked(currentState: SettingsViewState) {
        if (currentState.isAdminModeEnabled) {
            resetAdminMode()
        } else {
            showEnterAdminModePasswordDialog()
        }
    }

    private fun handleOnCookiesAllowedItemClicked(currentState: SettingsViewState) = viewModelScopeIO.launch {
        webViewOptionInteractor.setIsAllowedCookies(!currentState.isCookiesAllowed)
    }

    private fun handleOnCacheAllowedItemClicked(currentState: SettingsViewState) = viewModelScopeIO.launch {
        webViewOptionInteractor.setIsAllowedCache(!currentState.isCacheAllowed)
    }

    private fun handleOnSelectShopItemClicked() {
        mainActivityRouter.navigateTo(ShopsScreen(ShopsFragment.Companion.AfterChoseShopRouting.EXIT))
    }

    private fun handleOnSelectPrinterItemClicked() {
        searchDevices { printerDevices ->
            processDataEvent(SettingsDataEvent.OnPrinterDevicesReceived(printerDevices))
        }
    }

    private fun handleOnSelectScannerItemClicked() {
        searchDevices { scannerDevices ->
            processDataEvent(SettingsDataEvent.OnScannerDevicesReceived(scannerDevices))
        }
    }

    private fun handleOnScanModeItemClicked(currentState: SettingsViewState) {
        navDrawerRouter.addTo(
            ListDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_scan_mode),
                listDialogItems = ListDialogItems(currentState.scanModeItems),
                onItemClicked = { scanModeId ->
                    handleSelectScanMode(scanModeId)
                }
            )
        )
    }

    private fun handleSelectScanMode(scanModeId: Int) = viewModelScopeIO.launch {
        webViewOptionInteractor.saveScanMode(scanModeId)
    }

    private fun searchDevices(onDevicesFounded: (List<Device>) -> Unit) {
        showSearchDevicesWaitingDialog()
        launchInJob {
            deviceInteractor.getBoundedDevices().fold(
                onSuccess = { devices ->
                    onDevicesFounded(devices)
                },
                onFailure = { error ->
                    if (error is BluetoothDeviceException) {
                        handleBluetoothDeviceException(error)
                    } else {
                        handleBasicException(error)
                    }
                }
            )
            navDrawerRouter.dismissAllDialogs()
        }
    }

    private fun showSearchDevicesWaitingDialog() {
        navDrawerRouter.addTo(
            WaitingDialogScreen(
                message = stringResourceProvider.getStringResource(R.string.text_dialog_loading_bluetooth_devices),
                closeButtonText = stringResourceProvider.getStringResource(R.string.text_close),
                onCancelAction = { cancelCurrentJob() }
            )
        )
    }

    private fun handleOnPrinterDevicesReceived(
        event: SettingsDataEvent.OnPrinterDevicesReceived,
        currentState: SettingsViewState
    ) {
        val printerDevices = event.printerDevices
        val listDialogItems = printerDevices.toListDialogItems(currentState.currentPrinter)
        navDrawerRouter.addTo(
            ListDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_printer),
                listDialogItems = ListDialogItems(listDialogItems),
                onItemClicked = { itemHashCode ->
                    handleSelectPrinterDevice(printerDevices, itemHashCode)
                }
            )
        )
    }

    private fun handleOnScannerDevicesReceived(
        event: SettingsDataEvent.OnScannerDevicesReceived,
        currentState: SettingsViewState
    ) {
        val scannerDevices = event.scannerDevices
        val listDialogItems = scannerDevices.toListDialogItems(currentState.currentScanner)
        navDrawerRouter.addTo(
            ListDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_scanner),
                listDialogItems = ListDialogItems(listDialogItems),
                onItemClicked = { itemHashCode ->
                    handleSelectScannerDevice(scannerDevices, itemHashCode)
                }
            )
        )
    }

    private fun handleOnSelectOrientationItemClicked(currentState: SettingsViewState) {
        navDrawerRouter.addTo(
            ListDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_screen_orientation),
                listDialogItems = ListDialogItems(currentState.orientationItems),
                onItemClicked = { screenOrientation ->
                    handleSelectScreenOrientation(screenOrientation)
                }
            )
        )
    }

    private fun handleSelectScreenOrientation(screenOrientation: Int) = viewModelScopeIO.launch {
        screenOrientationInteractor.setScreenOrientation(screenOrientation)
    }

    private fun handleOnCacheModeItemClicked(currentState: SettingsViewState) {
        navDrawerRouter.addTo(
            ListDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_cache_mode),
                listDialogItems = ListDialogItems(currentState.cachedModeItems),
                onItemClicked = { cacheMode ->
                    handleSelectCacheMode(cacheMode)
                }
            )
        )
    }

    private fun handleSelectCacheMode(cacheMode: Int) = viewModelScopeIO.launch {
        webViewOptionInteractor.saveCacheMode(cacheMode)
    }

    private fun showEnterAdminModePasswordDialog() {
        navDrawerRouter.addTo(
            EditTextDialogScreen(
                title = stringResourceProvider.getStringResource(R.string.text_admin_mode_enter_password),
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
                positiveButtonText = stringResourceProvider.getStringResource(R.string.text_button_activate),
                negativeButtonText = stringResourceProvider.getStringResource(R.string.text_button_cancel),
                positiveButtonAction = { adminModePassword ->
                    activateAdminMode(adminModePassword)
                }
            )
        )
    }

    private fun activateAdminMode(adminModePassword: String) = viewModelScopeIO.launch {
        adminModeInteractor.activateAdminMode(adminModePassword).fold(
            onSuccess = {
                navDrawerRouter.showSnackBar(stringResourceProvider.getStringResource(R.string.text_admin_mode_enabled))
            },
            onFailure = { error ->
                if (error is AdminModeException) {
                    handleAdminModeException(error)
                } else {
                    handleBasicException(error)
                }
            }
        )
    }

    private fun resetAdminMode() = viewModelScopeIO.launch {
        adminModeInteractor.resetAdminMode().onFailure(::handleBasicException)
    }

    private fun handleSelectPrinterDevice(printerDevices: List<Device>, itemHashCode: Int) = viewModelScopeIO.launch {
        findDeviceByHashCode(printerDevices, itemHashCode)
            ?.let { printerDevice -> deviceInteractor.setSelectedPrinter(printerDevice) }
            ?: run { deviceInteractor.deletePrinter() }
    }

    private fun handleSelectScannerDevice(scannerDevices: List<Device>, itemHashCode: Int) = viewModelScopeIO.launch {
        findDeviceByHashCode(scannerDevices, itemHashCode)
            ?.let { scannerDevice -> deviceInteractor.setSelectedScanner(scannerDevice) }
            ?: run { deviceInteractor.deleteScanner() }
    }

    private fun handleAdminModeException(error: AdminModeException) {
        val errorMessageResourceId = when (error) {
            AdminModeException.PasswordEmpty -> R.string.text_error_password_empty
            AdminModeException.PasswordWrong -> R.string.text_error_password_wrong
        }
        navDrawerRouter.showErrorSnackbar(
            errorText = stringResourceProvider.getStringResource(errorMessageResourceId)
        )
    }

    private fun handleBluetoothDeviceException(error: BluetoothDeviceException) {
        val errorMessageResourceId = when (error) {
            BluetoothDeviceException.EmptyDevicesList -> R.string.text_error_bluetooth_device_not_found
            BluetoothDeviceException.FailedToEnable -> R.string.text_error_enable_bluetooth
        }
        navDrawerRouter.showErrorSnackbar(
            errorText = stringResourceProvider.getStringResource(errorMessageResourceId)
        )
    }

    private fun handleBasicException(error: Throwable) {
        Timber.e(error)
        if (error !is kotlinx.coroutines.CancellationException) {
            navDrawerRouter.showErrorSnackbar(
                errorText = error.message.orEmpty(),
                errorDetails = error.cause?.message.orEmpty()
            )
        }
    }

    private fun launchInJob(action: suspend () -> Unit) {
        cancelCurrentJob()
        currentJob = viewModelScopeIO.launch {
            action()
        }
    }

    private fun cancelCurrentJob() {
        currentJob?.cancel()
        currentJob = null
    }

    private fun List<Device>.toListDialogItems(currentDevice: Device?): List<ListDialogItem> {
        val listDialogItems = map { device ->
            ListDialogItem(
                id = device.hashCode(),
                name = device.name,
                isSelected = device == currentDevice
            )
        }

        return buildList {
            add(
                ListDialogItem(
                    id = -1,
                    name = stringResourceProvider.getStringResource(R.string.text_not_selected),
                    isSelected = currentDevice == null
                )
            )
            addAll(listDialogItems)
        }
    }

    private fun findDeviceByHashCode(devices: List<Device>, hashCode: Int): Device? {
        return devices.find { device ->
            device.hashCode() == hashCode
        }
    }

    override fun onCleared() {
        CoroutineScope(Dispatchers.IO).launch {
            adminModeInteractor.resetAdminMode()
        }
        super.onCleared()
    }
}
