package ru.perekrestok.wms_native_mobile.screens.settings

import android.text.InputType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.perekrestok.android.dialogs.alert.AlertDialogScreen
import ru.perekrestok.android.dialogs.edit_text.EditTextDialogScreen
import ru.perekrestok.android.dialogs.list.ListDialogScreen
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItems
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.exception.AdminModeException
import ru.perekrestok.domain.interactor.AdminModeInteractor
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
import ru.perekrestok.wms_native_mobile.screens.search_device.DeviceType
import ru.perekrestok.wms_native_mobile.screens.search_device.SearchDeviceScreen
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
        mainActivityRouter.navigateTo(SearchDeviceScreen(DeviceType.PRINTER))
    }

    private fun handleOnSelectScannerItemClicked() {
        mainActivityRouter.navigateTo(SearchDeviceScreen(DeviceType.SCANNER))
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
            onFailure = { error -> handleError(error) }
        )
    }

    private fun resetAdminMode() = viewModelScopeIO.launch {
        adminModeInteractor.resetAdminMode().onFailure(::handleError)
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is AdminModeException -> handleAdminModeException(error)
            else -> handleBasicException(error)
        }
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

    private fun handleBasicException(error: Throwable) {
        Timber.e(error)
        if (error !is kotlinx.coroutines.CancellationException) {
            navDrawerRouter.showErrorSnackbar(
                errorText = error.message.orEmpty(),
                errorDetails = error.cause?.message.orEmpty()
            )
        }
    }

    override fun onCleared() {
        CoroutineScope(Dispatchers.IO).launch {
            adminModeInteractor.resetAdminMode()
        }
        super.onCleared()
    }
}
