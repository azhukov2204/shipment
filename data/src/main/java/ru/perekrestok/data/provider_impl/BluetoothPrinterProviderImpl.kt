package ru.perekrestok.data.provider_impl

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.perekrestok.android.extension.bluetoothManager
import ru.perekrestok.domain.entity.AppSetting.Companion.getSelectedPrinter
import ru.perekrestok.domain.entity.DeviceConnectionState
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.exception.PrinterException
import ru.perekrestok.domain.provider.BluetoothDeviceProvider
import ru.perekrestok.domain.provider.BluetoothPrinterProvider
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import timber.log.Timber
import java.nio.charset.Charset
import java.util.UUID

class BluetoothPrinterProviderImpl(
    private val context: Context,
    private val bluetoothDeviceProvider: BluetoothDeviceProvider,
    private val settingsRepository: LocalAppSettingsRepository
) : BluetoothPrinterProvider {

    companion object {
        private val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val DEFAULT_ENCODING = "Windows-1251"
    }

    private val mutex = Mutex()

    private val bluetoothAdapter: BluetoothAdapter
        get() = context.bluetoothManager.adapter

    private var printer: BluetoothDevice? = null
    private var printerBluetoothSocket: BluetoothSocket? = null

    private val _printerConnectionMutableStateFlow: MutableStateFlow<DeviceConnectionState> =
        MutableStateFlow(DeviceConnectionState.NO_DEVICE)

    override val printerConnectionStateFlow: StateFlow<DeviceConnectionState> =
        _printerConnectionMutableStateFlow.asStateFlow()

    override suspend fun connect(): Unit = mutex.withLock {
        try {
            val currentPrinterSetting = settingsRepository.getSettings().getSelectedPrinter()
            if (currentPrinterSetting != null) {
                _printerConnectionMutableStateFlow.emit(DeviceConnectionState.CONNECTING)
                bluetoothDeviceProvider.tryEnableBluetooth()
                val newPrinter = bluetoothAdapter.getRemoteDevice(currentPrinterSetting.address)
                setNewPrinter(newPrinter)

                val printer = printer
                if (printerBluetoothSocket == null && printer != null) {
                    printerBluetoothSocket = printer.createInsecureRfcommSocketToServiceRecord(uuid)
                }

                printerBluetoothSocket?.let { printerBluetoothSocket ->
                    if (!printerBluetoothSocket.isConnected) printerBluetoothSocket.connect()
                    _printerConnectionMutableStateFlow.emit(DeviceConnectionState.CONNECTED)
                }
            } else {
                _printerConnectionMutableStateFlow.emit(DeviceConnectionState.NO_DEVICE)
                throw PrinterException.NoPrinter
            }

        } catch (error: Throwable) {
            Timber.e(error)
            _printerConnectionMutableStateFlow.emit(DeviceConnectionState.DISCONNECTED)
            throw when (error) {
                is BluetoothDeviceException, is PrinterException -> error
                else -> PrinterException.CouldNotOpenConnection(message = error.message.orEmpty())
            }

        }

    }

    override suspend fun disconnect() {
        printerBluetoothSocket?.close()
        printerBluetoothSocket = null
        val currentPrinterSetting = settingsRepository.getSettings().getSelectedPrinter()
        val connectionState = if (currentPrinterSetting != null) {
            DeviceConnectionState.DISCONNECTED
        } else {
            DeviceConnectionState.NO_DEVICE
        }
        _printerConnectionMutableStateFlow.emit(connectionState)
    }

    override suspend fun sendData(
        data: String,
        encoding: String?
    ): Unit = mutex.withLock {
        printerBluetoothSocket?.let { printerBluetoothSocket ->
            val outputStream = printerBluetoothSocket.outputStream
            val byteArray =
                data.toByteArray(Charset.forName(encoding ?: DEFAULT_ENCODING))
            outputStream.write(byteArray)
            outputStream.flush()
        }
    }

    private suspend fun setNewPrinter(newPrinter: BluetoothDevice?) {
        if (printer != newPrinter) {
            disconnect()
            printer = newPrinter
        }
    }

    override suspend fun sendDataLonely(data: String, encoding: String?) {
        connect()
        sendData(data, encoding)
        disconnect()
    }
}