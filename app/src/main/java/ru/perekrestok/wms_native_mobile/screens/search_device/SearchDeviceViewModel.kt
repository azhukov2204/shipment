package ru.perekrestok.wms_native_mobile.screens.search_device

import kotlinx.coroutines.launch
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.android.view_model.event.OnButtonBackClicked
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.activity.MainActivityRouter

class SearchDeviceViewModel(
    private val deviceType: DeviceType,
    private val deviceInteractor: DeviceInteractor,
    private val stringResourceProvider: StringResourceProvider,
    private val mainActivityRouter: MainActivityRouter,
    private val settingsInteractor: SettingsInteractor
) : BaseViewModel<SearchDeviceViewState>() {

    init {
        viewModelScopeIO.launch {
            deviceInteractor.searchDevicesFlow.collect { devices ->
                processDataEvent(SearchDeviceDataEvent.OnDevicesReceived(devices))
            }
        }

        viewModelScopeIO.launch {
            settingsInteractor.getAppSettingsFlow().collect { settings ->
                processDataEvent(SearchDeviceDataEvent.OnAppSettingsReceived(settings))
            }
        }
    }

    override fun initialViewState(): SearchDeviceViewState = SearchDeviceViewState(
        deviceType = deviceType,
        stringResourceProvider = stringResourceProvider
    )

    override suspend fun reduce(event: Event, currentState: SearchDeviceViewState): SearchDeviceViewState =
        when (event) {
            OnButtonBackClicked -> dispatchBackEvent(mainActivityRouter, currentState)
            is LifecycleEvent -> dispatchLifecycleEvent(event, currentState)
            is SearchDeviceUiEvent -> dispatchSearchDeviceUiEvent(event, currentState)
            is SearchDeviceDataEvent -> dispatchSearchDeviceDataEvent(event, currentState)
            else -> currentState
        }

    private fun dispatchLifecycleEvent(
        event: LifecycleEvent,
        currentState: SearchDeviceViewState
    ): SearchDeviceViewState = when (event) {
        LifecycleEvent.OnLifecycleOwnerStart -> currentState.also {
            handleOnLifecycleOwnerStart()
        }
        LifecycleEvent.OnLifecycleOwnerStop -> currentState.also {
            handleOnLifecycleOwnerStop()
        }
        else -> currentState
    }

    private fun dispatchSearchDeviceUiEvent(
        event: SearchDeviceUiEvent,
        currentState: SearchDeviceViewState
    ): SearchDeviceViewState = when (event) {
        SearchDeviceUiEvent.OnCloseButtonClicked -> dispatchBackEvent(mainActivityRouter, currentState)
        is SearchDeviceUiEvent.OnDeviceItemClicked -> currentState.also {
            handleOnDeviceItemClicked(currentState, event)
        }
    }

    private fun dispatchSearchDeviceDataEvent(
        event: SearchDeviceDataEvent,
        currentState: SearchDeviceViewState
    ): SearchDeviceViewState = when (event) {
        is SearchDeviceDataEvent.OnDevicesReceived -> currentState.copy(
            devices = buildSet {
                addAll(currentState.devices)
                addAll(event.devices)
            }
        )
        is SearchDeviceDataEvent.OnErrorHappened -> currentState.copy(
            errorMessage = event.errorMessage
        )
        is SearchDeviceDataEvent.OnAppSettingsReceived -> currentState.copy(
            settings = event.settings
        )
    }

    private fun handleOnLifecycleOwnerStart() = viewModelScopeIO.launch {
        deviceInteractor.startSearchDevices().onFailure { error ->
            handleError(error)
        }
    }

    private fun handleOnLifecycleOwnerStop() = viewModelScopeIO.launch {
        deviceInteractor.stopSearchDevices().onFailure { error ->
            handleError(error)
        }
    }

    private fun handleError(error: Throwable) {
        val errorMessage = when (error) {
            is BluetoothDeviceException -> getBluetoothDeviceExceptionMessage(error)
            else -> error.message.orEmpty()
        }

        processDataEvent(SearchDeviceDataEvent.OnErrorHappened(errorMessage))
    }

    private fun getBluetoothDeviceExceptionMessage(error: BluetoothDeviceException): String {
        val errorTestResId = when (error) {
            BluetoothDeviceException.FailedToEnable -> R.string.text_error_enable_bluetooth
            BluetoothDeviceException.SearchDeviceNotGranted -> R.string.text_error_search_devices_no_granted
        }
        return stringResourceProvider.getStringResource(errorTestResId)
    }

    private fun handleOnDeviceItemClicked(
        currentState: SearchDeviceViewState,
        event: SearchDeviceUiEvent.OnDeviceItemClicked
    ) = viewModelScopeIO.launch {
        val foundedDevice = findDeviceByHashCode(devices = currentState.devices, hashCode = event.deviceHashCode)
        when (currentState.deviceType) {
            DeviceType.PRINTER ->
                foundedDevice
                    ?.let { deviceInteractor.setSelectedPrinter(foundedDevice) }
                    ?: run { deviceInteractor.deletePrinter() }

            DeviceType.SCANNER ->
                foundedDevice
                    ?.let { deviceInteractor.setSelectedScanner(foundedDevice) }
                    ?: run { deviceInteractor.deleteScanner() }
        }
        mainActivityRouter.exit()
    }

    private fun findDeviceByHashCode(devices: Set<Device>, hashCode: Int): Device? {
        return devices.find { device ->
            device.hashCode() == hashCode
        }
    }
}
