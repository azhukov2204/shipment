package ru.perekrestok.domain.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.perekrestok.domain.entity.DeviceConnectionState

interface BluetoothScannerProvider {
    val scanResultFlow: Flow<String>
    val scannerConnectionStateFlow: StateFlow<DeviceConnectionState>
    suspend fun establishConnectionSafely()
    suspend fun disconnect(): Result<Unit>
}
