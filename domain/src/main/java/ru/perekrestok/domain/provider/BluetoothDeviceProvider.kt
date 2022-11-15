package ru.perekrestok.domain.provider

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.Device

interface BluetoothDeviceProvider {
    val searchDevicesFlow: Flow<Set<Device>>
    suspend fun tryEnableBluetooth()
    suspend fun startSearchDevices()
    suspend fun stopSearchDevices()
}
