package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.entity.DeviceConnectionState
import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.provider.BluetoothDeviceProvider
import ru.perekrestok.domain.provider.BluetoothPrinterProvider
import ru.perekrestok.domain.provider.BluetoothScannerProvider
import ru.perekrestok.domain.repository.LocalAppSettingsRepository

interface DeviceInteractor : ScannerInteractor, PrinterInteractor {
    val searchDevicesFlow: Flow<Set<Device>>
    suspend fun startSearchDevices(): Result<Unit>
    suspend fun stopSearchDevices(): Result<Unit>
}

interface ScannerInteractor {
    val scannerConnectionStateFlow: StateFlow<DeviceConnectionState>
    val scanResultFlow: Flow<String>
    suspend fun setSelectedScanner(scannerDevice: Device)
    suspend fun deleteScanner()
    suspend fun setIsInternalScannerEnabled(isEnabled: Boolean)
    suspend fun setScanMode(scanMode: ScanMode)
}

interface PrinterInteractor {
    val printerConnectionStateFlow: StateFlow<DeviceConnectionState>
    suspend fun setSelectedPrinter(printerDevice: Device)
    suspend fun deletePrinter()
    suspend fun connectToPrinter(): Result<Unit>
    suspend fun printData(data: String, encoding: String?): Result<Unit>
    suspend fun disconnectFromPrinter(): Result<Unit>
    suspend fun printDataLonely(data: String, encoding: String?): Result<Unit>
}

class DeviceInteractorImpl(
    private val bluetoothDeviceProvider: BluetoothDeviceProvider,
    private val settingsRepository: LocalAppSettingsRepository,
    private val bluetoothScannerProvider: BluetoothScannerProvider,
    private val bluetoothPrinterProvider: BluetoothPrinterProvider,
    private val localAppSettingsRepository: LocalAppSettingsRepository,
) : DeviceInteractor {

    override val scannerConnectionStateFlow: StateFlow<DeviceConnectionState> =
        bluetoothScannerProvider.scannerConnectionStateFlow

    override val printerConnectionStateFlow: StateFlow<DeviceConnectionState> =
        bluetoothPrinterProvider.printerConnectionStateFlow

    override val searchDevicesFlow: Flow<Set<Device>> =
        bluetoothDeviceProvider.searchDevicesFlow

    override suspend fun startSearchDevices(): Result<Unit> = runCatching {
        bluetoothDeviceProvider.startSearchDevices()
    }

    override suspend fun stopSearchDevices(): Result<Unit> = runCatching {
        bluetoothDeviceProvider.stopSearchDevices()
    }

    override suspend fun setSelectedPrinter(printerDevice: Device) {
        settingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SELECTED_PRINTER,
                value = printerDevice
            )
        )
    }

    override suspend fun deletePrinter() {
        settingsRepository.deleteSetting(SettingKey.SELECTED_PRINTER)
        disconnectFromPrinter()
    }

    override suspend fun setSelectedScanner(scannerDevice: Device) {
        bluetoothScannerProvider.disconnect()
        settingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SELECTED_SCANNER,
                value = scannerDevice
            )
        )
    }

    override suspend fun deleteScanner() {
        settingsRepository.deleteSetting(SettingKey.SELECTED_SCANNER)
        bluetoothScannerProvider.disconnect()
    }

    override suspend fun connectToPrinter(): Result<Unit> = runCatching {
        bluetoothPrinterProvider.connect()
    }

    override suspend fun printData(data: String, encoding: String?) = runCatching {
        bluetoothPrinterProvider.sendData(data, encoding)
    }

    override suspend fun disconnectFromPrinter(): Result<Unit> = runCatching {
        bluetoothPrinterProvider.disconnect()
    }

    override suspend fun printDataLonely(data: String, encoding: String?) = runCatching {
        bluetoothPrinterProvider.sendDataLonely(data, encoding)
    }

    override val scanResultFlow: Flow<String> = bluetoothScannerProvider.scanResultFlow

    override suspend fun setIsInternalScannerEnabled(isEnabled: Boolean) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.IS_USE_INTERNAL_SCANNER,
                value = isEnabled
            )
        )
    }

    override suspend fun setScanMode(scanMode: ScanMode) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SCAN_MODE_ID,
                value = scanMode.id
            )
        )
    }
}
