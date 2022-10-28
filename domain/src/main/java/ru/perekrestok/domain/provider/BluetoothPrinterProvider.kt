package ru.perekrestok.domain.provider

import kotlinx.coroutines.flow.StateFlow
import ru.perekrestok.domain.entity.DeviceConnectionState

interface BluetoothPrinterProvider {
    val printerConnectionStateFlow: StateFlow<DeviceConnectionState>
    suspend fun disconnect()
    suspend fun connect()
    suspend fun sendData(data: String, encoding: String?)
    suspend fun sendDataLonely(data: String, encoding: String?)
}
