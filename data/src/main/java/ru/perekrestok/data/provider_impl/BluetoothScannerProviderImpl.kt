package ru.perekrestok.data.provider_impl

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.perekrestok.android.extension.bluetoothManager
import ru.perekrestok.data.worker.BluetoothScannerWorker
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedScanner
import ru.perekrestok.domain.entity.DeviceConnectionState
import ru.perekrestok.domain.provider.BluetoothDeviceProvider
import ru.perekrestok.domain.provider.BluetoothScannerProvider
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.kotlin.StringPatterns
import timber.log.Timber
import java.util.UUID

internal class BluetoothScannerProviderImpl(
    private val context: Context,
    private val bluetoothDeviceProvider: BluetoothDeviceProvider,
    private val settingsRepository: LocalAppSettingsRepository
) : BluetoothScannerProvider, BluetoothScannerWorker() {

    companion object {
        private const val REQUEST_DELAY_MILLIS = 5_000L
        private val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val BUFFER_SIZE: Int = 256
        private const val SCAN_END_CHAR: Char = '\r'
    }

    private val bluetoothAdapter: BluetoothAdapter
        get() = context.bluetoothManager.adapter

    private var scanner: BluetoothDevice? = null
    private var scannerBluetoothSocket: BluetoothSocket? = null

    private val _scannerConnectionMutableStateFlow: MutableStateFlow<DeviceConnectionState> =
        MutableStateFlow(DeviceConnectionState.NO_DEVICE)

    override suspend fun requestDelayMillis(): Long = REQUEST_DELAY_MILLIS

    private val _scanResultMutableFlow: MutableSharedFlow<String> = MutableSharedFlow()

    override val scanResultFlow: Flow<String> = _scanResultMutableFlow.asSharedFlow()

    override val scannerConnectionStateFlow: StateFlow<DeviceConnectionState> =
        _scannerConnectionMutableStateFlow.asStateFlow()

    private val mutex = Mutex()

    init {
        scope.launch {
            settingsRepository.getSettingsFlow()
                .map { it.getSelectedScanner() }
                .distinctUntilChanged()
                .collect { currentScanner ->
                    currentScanner?.let { putData(currentScanner) }
                        ?: run {
                            _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.NO_DEVICE)
                            removeData()
                        }
                }
        }
    }

    override suspend fun sync() {
        if (scannerConnectionStateFlow.value != DeviceConnectionState.CONNECTED &&
            !mutex.isLocked
        ) {
            establishConnectionSafely()
        }
    }

    override suspend fun stopSync() {
        disconnect()
    }

    override suspend fun establishConnectionSafely(): Unit = mutex.withLock {
        runCatching {
            data?.let { currentScannerSetting ->
                _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.CONNECTING)
                bluetoothDeviceProvider.tryEnableBluetooth()
                val newScanner = bluetoothAdapter.getRemoteDevice(currentScannerSetting.address)
                setNewScanner(newScanner)
                connect(newScanner)
                runInputStreamListener()
            }
        }.onFailure { error ->
            Timber.e(error)
            _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.DISCONNECTED)
        }
    }

    private suspend fun setNewScanner(newScanner: BluetoothDevice) {
        if (scanner != newScanner) {
            disconnect()
            scanner = newScanner
        }
    }

    private suspend fun connect(scanner: BluetoothDevice) {
        runCatching {
            if (scannerBluetoothSocket == null) {
                scannerBluetoothSocket = scanner.createInsecureRfcommSocketToServiceRecord(uuid)
            }

            scannerBluetoothSocket?.let { scannerBluetoothSocket ->
                if (!scannerBluetoothSocket.isConnected) scannerBluetoothSocket.connect()
            }
        }.onFailure { error ->
            Timber.e(error)
            _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.DISCONNECTED)
        }
    }

    private suspend fun runInputStreamListener() {
        runCatching {
            val socket = scannerBluetoothSocket
            if (socket != null && socket.isConnected) {

                _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.CONNECTED)
                while (true) {
                    val buffer = ByteArray(BUFFER_SIZE)

                    val scanResult: StringBuilder = StringBuilder(StringPatterns.EMPTY_SYMBOL)

                    do {
                        val length = socket.inputStream.read(buffer)
                        val bufferText = String(buffer, 0, length)
                        scanResult.append(bufferText)
                    } while (bufferText.last() != SCAN_END_CHAR)
                    _scanResultMutableFlow.emit(scanResult.toString())
                }
            }
        }.onFailure { error ->
            Timber.e(error)
            disconnect()
        }
    }

    override suspend fun disconnect(): Result<Unit> = runCatching {
        _scannerConnectionMutableStateFlow.emit(DeviceConnectionState.DISCONNECTED)
        scannerBluetoothSocket?.close()
        scannerBluetoothSocket = null

        val currentScannerSetting = settingsRepository.getSettings().getSelectedScanner()
        val connectionState = if (currentScannerSetting != null) {
            DeviceConnectionState.DISCONNECTED
        } else {
            DeviceConnectionState.NO_DEVICE
        }
        _scannerConnectionMutableStateFlow.emit(connectionState)
    }
}
