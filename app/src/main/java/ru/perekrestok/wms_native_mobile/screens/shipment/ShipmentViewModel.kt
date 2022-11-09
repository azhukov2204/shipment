package ru.perekrestok.wms_native_mobile.screens.shipment

import kotlinx.coroutines.launch
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.android.view_model.event.OnButtonBackClicked
import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.ScannerInteractor
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.interactor.WebViewOptionInteractor
import ru.perekrestok.domain.interactor.WireCommand
import ru.perekrestok.domain.interactor.WireInteractor
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerRouter
import ru.perekrestok.wms_native_mobile.screens.shipment.web_view.ShipmentJSViewModel

@Suppress("LongParameterList")
class ShipmentViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val deviceInteractor: DeviceInteractor,
    private val wireInteractor: WireInteractor,
    private val scannerInteractor: ScannerInteractor,
    webViewOptionInteractor: WebViewOptionInteractor,
    systemActionInteractor: SystemActionInteractor,
    navDrawerRouter: NavDrawerRouter,
    stringResourceProvider: StringResourceProvider
) : ShipmentJSViewModel(
    systemActionInteractor = systemActionInteractor,
    navDrawerRouter = navDrawerRouter,
    settingsInteractor = settingsInteractor,
    deviceInteractor = deviceInteractor,
    stringResourceProvider = stringResourceProvider,
    webViewOptionInteractor = webViewOptionInteractor,
    scannerInteractor = scannerInteractor
) {

    init {
        viewModelScopeIO.launch {
            settingsInteractor.getAppSettingsFlow().collect { settings ->
                processDataEvent(ShipmentDataEvent.OnAppSettingsReceived(settings))
            }
        }

        viewModelScopeIO.launch {
            settingsInteractor.getCustomSettingsFlow().collect { settings ->
                processDataEvent(ShipmentDataEvent.OnCustomSettingsReceived(settings))
            }
        }

        viewModelScopeIO.launch {
            deviceInteractor.printerConnectionStateFlow.collect { printerConnectionState ->
                processDataEvent(ShipmentDataEvent.OnPrinterConnectionStateReceived(printerConnectionState))
            }
        }

        viewModelScopeIO.launch {
            wireInteractor.commandFlow.collect { wireCommand ->
                handleWireCommand(wireCommand)
            }
        }

        viewModelScopeIO.launch {
            scannerInteractor.scanResultFlow.collect { barcode ->
                processDataEvent(ShipmentDataEvent.OnBarcodeReceived(barcode))
            }
        }
    }

    private fun handleWireCommand(wireCommand: WireCommand) = when (wireCommand) {
        WireCommand.ClearCache -> clearCache()
    }

    override fun initialViewState(): ShipmentViewState = ShipmentViewState()

    override suspend fun reduce(event: Event, currentState: ShipmentViewState): ShipmentViewState = when (event) {
        OnButtonBackClicked -> currentState.also { handleOnButtonBackClicked() }
        is ShipmentDataEvent -> dispatchShipmentDataEvent(event, currentState)
        is ShipmentUiEvent -> dispatchShipmentUiEvent(event, currentState)
        else -> currentState
    }

    private fun dispatchShipmentUiEvent(event: ShipmentUiEvent, currentState: ShipmentViewState): ShipmentViewState =
        when (event) {
            ShipmentUiEvent.ReloadPage -> currentState.also {
                handleReloadPage()
            }
            is ShipmentUiEvent.OnPageLoadingProgressReceived -> currentState.copy(loadingProgress = event.progress)
            ShipmentUiEvent.OnPageLoaded -> currentState.copy(isPageLoaded = true)
            ShipmentUiEvent.ClearCookies -> currentState.also {
                clearCookies()
            }
            ShipmentUiEvent.ClearCache -> currentState.also {
                clearCache()
            }
        }

    private fun handleReloadPage() {
        processSingleEvent(ShipmentSingleEvent.ReloadData)
    }

    private fun dispatchShipmentDataEvent(
        event: ShipmentDataEvent,
        currentState: ShipmentViewState
    ): ShipmentViewState = when (event) {
        is ShipmentDataEvent.OnAppSettingsReceived -> currentState.copy(appSettings = event.appSettings)
        is ShipmentDataEvent.OnCustomSettingsReceived -> currentState.copy(customSettings = event.customSettings)
        is ShipmentDataEvent.OnPrinterConnectionStateReceived -> currentState.copy(
            printerConnectionState = event.printerConnectionState
        )
        is ShipmentDataEvent.OnBarcodeReceived -> currentState.also {
            handleOnBarcodeReceived(currentState, event)
        }
    }

    private fun handleOnBarcodeReceived(currentState: ShipmentViewState, event: ShipmentDataEvent.OnBarcodeReceived) {
        val renderBarcodeJs = scannerInteractor.getRenderBarcodeJs(event.barcode, currentState.scanMode)
        val isNeedPressEnter = when (currentState.scanMode) {
            ScanMode.TO_ACTIVE_FIELD -> true
            ScanMode.RUN_JS -> false
        }
        processSingleEvent(
            ShipmentSingleEvent.ExecuteJSCommand(
                renderBarcodeJs = renderBarcodeJs,
                isNeedPressEnter = isNeedPressEnter
            )
        )
    }

    private fun handleOnButtonBackClicked() {
        val renderBarcodeJs = scannerInteractor.getBackClickJS()
        processSingleEvent(
            ShipmentSingleEvent.ExecuteJSCommand(
                renderBarcodeJs = renderBarcodeJs,
                isNeedPressEnter = false
            )
        )
    }
}
