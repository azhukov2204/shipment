package ru.perekrestok.wms_native_mobile.screens.nav_drawer_container

import androidx.annotation.ColorRes
import com.github.terrakok.cicerone.androidx.FragmentScreen
import kotlinx.coroutines.launch
import ru.perekrestok.android.dialogs.message.MessageDialogScreen
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.screens.settings.SettingsScreen
import ru.perekrestok.wms_native_mobile.screens.shipment.ShipmentScreen

class NavDrawerViewModel(
    private val navDrawerRouter: NavDrawerRouter,
    private val deviceInteractor: DeviceInteractor,
    private val stringResourceProvider: StringResourceProvider,
    private val settingsInteractor: SettingsInteractor,
    private val systemActionInteractor: SystemActionInteractor
) : BaseViewModel<NavDrawerViewState>() {

    init {
        viewModelScopeIO.launch {
            deviceInteractor.scannerConnectionStateFlow.collect { scannerConnectionState ->
                processDataEvent(NavDrawerDataEvent.OnScannerConnectionStateReceived(scannerConnectionState))
            }
        }

        viewModelScopeIO.launch {
            deviceInteractor.printerConnectionStateFlow.collect { printerConnectionState ->
                processDataEvent(NavDrawerDataEvent.OnPrinterConnectionStateReceived(printerConnectionState))
            }
        }

        viewModelScopeIO.launch {
            settingsInteractor.getAppSettingsFlow().collect { settings ->
                processDataEvent(NavDrawerDataEvent.OnSettingsReceived(settings))
            }
        }
    }

    override fun initialViewState(): NavDrawerViewState =
        NavDrawerViewState(stringResourceProvider = stringResourceProvider)

    override suspend fun reduce(event: Event, currentState: NavDrawerViewState): NavDrawerViewState = when (event) {
        is LifecycleEvent -> dispatchLifecycleEvent(event, currentState)
        is NavDrawerUiEvent -> dispatchNavDriverUiEvent(event, currentState)
        is NavDrawerDataEvent -> dispatchNavDrawerDataEvent(event, currentState)
        else -> currentState
    }

    private fun dispatchLifecycleEvent(
        event: LifecycleEvent,
        currentState: NavDrawerViewState
    ): NavDrawerViewState = when (event) {
        LifecycleEvent.OnLifecycleOwnerCreate -> handleOnLifecycleOwnerCreate(currentState)
        else -> currentState
    }

    private fun dispatchNavDrawerDataEvent(
        event: NavDrawerDataEvent,
        currentState: NavDrawerViewState
    ): NavDrawerViewState = when (event) {
        is NavDrawerDataEvent.OnScannerConnectionStateReceived -> currentState.copy(
            scannerConnectionState = event.deviceConnectionState
        )
        is NavDrawerDataEvent.OnPrinterConnectionStateReceived -> currentState.copy(
            printerConnectionState = event.deviceConnectionState
        )
        is NavDrawerDataEvent.OnSettingsReceived -> currentState.copy(appSettings = event.settings)
    }

    private fun handleOnLifecycleOwnerCreate(currentState: NavDrawerViewState): NavDrawerViewState {
        return if (currentState.isAlreadyInitialized.not()) {
            navDrawerRouter.newRootScreen(ShipmentScreen)
            currentState.copy(isAlreadyInitialized = true)
        } else {
            currentState
        }
    }

    private fun dispatchNavDriverUiEvent(
        event: NavDrawerUiEvent,
        currentState: NavDrawerViewState
    ): NavDrawerViewState = when (event) {
        is NavDrawerUiEvent.OnMenuItemClicked -> handleOnMenuItemClicked(event, currentState)
        NavDrawerUiEvent.OnMenuButtonClicked -> currentState.also { handleOnMenuButtonClicked() }
        NavDrawerUiEvent.OnAboutButtonClicked -> currentState.also { handleOnAboutButtonClicked(currentState) }
    }

    private fun handleOnMenuItemClicked(
        event: NavDrawerUiEvent.OnMenuItemClicked,
        currentState: NavDrawerViewState
    ): NavDrawerViewState {
        val (screen, titleResId, isToolbarVisible) = when (event.menuItemId) {
            R.id.menu_store -> NavigationParams(ShipmentScreen, R.string.text_scanning, false)
            R.id.menu_settings -> NavigationParams(SettingsScreen, R.string.text_settings, true)
            else -> NavigationParams(ShipmentScreen, R.string.text_scanning, false)
        }

        return currentState.copy(
            toolbarTitle = stringResourceProvider.getStringResource(titleResId),
            isToolbarVisible = isToolbarVisible
        ).also { navDrawerRouter.newRootScreen(screen) }
    }

    private fun handleOnMenuButtonClicked() {
        processSingleEvent(NavDrawerSingleEvent.OpenNavigationDrawer)
    }

    private fun handleOnAboutButtonClicked(currentState: NavDrawerViewState) {
        val appCode = systemActionInteractor.getAppCode().toString()
        val appName = systemActionInteractor.getAppName()
        val installDate = systemActionInteractor.getDateInstallApp()
        val initDate = currentState.initDate

        val message = buildString {
            append(stringResourceProvider.getStringResource(R.string.text_version, appName, appCode))
            append(stringResourceProvider.getStringResource(R.string.text_install_date, installDate))
            append(stringResourceProvider.getStringResource(R.string.text_init_date, initDate))
        }
        navDrawerRouter.addTo(
            MessageDialogScreen(
                message = message,
                buttonText = stringResourceProvider.getStringResource(R.string.text_close)
            )
        )
    }

    private data class NavigationParams(
        val screen: FragmentScreen,
        @ColorRes val titleResId: Int,
        val isToolbarVisible: Boolean
    )
}
