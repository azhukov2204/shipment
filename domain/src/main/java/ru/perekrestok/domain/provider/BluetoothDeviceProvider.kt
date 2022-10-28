package ru.perekrestok.domain.provider

import ru.perekrestok.domain.entity.Device

interface BluetoothDeviceProvider {
    suspend fun tryEnableBluetooth()
    suspend fun getBondedDevices(): List<Device>
}
