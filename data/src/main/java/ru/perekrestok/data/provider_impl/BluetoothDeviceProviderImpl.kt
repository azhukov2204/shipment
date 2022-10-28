package ru.perekrestok.data.provider_impl

import android.bluetooth.BluetoothAdapter
import android.content.Context
import kotlinx.coroutines.delay
import ru.perekrestok.android.extension.bluetoothManager
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.provider.BluetoothDeviceProvider

internal class BluetoothDeviceProviderImpl(
    private val context: Context
) : BluetoothDeviceProvider {

    companion object {
        private const val TRY_BLUETOOTH_ENABLE_MAX_ATTEMPTS = 10
        private const val TRY_BLUETOOTH_ENABLE_DELAY = 1000L
    }

    private val bluetoothAdapter: BluetoothAdapter?
        get() = context.bluetoothManager.adapter

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override suspend fun getBondedDevices(): List<Device> {
        tryEnableBluetooth()
        val devices = bluetoothAdapter?.bondedDevices?.map { bluetoothDevice ->
            Device(
                name = bluetoothDevice.name,
                address = bluetoothDevice.address
            )
        }

        return if (devices != null && devices.isNotEmpty()) {
            devices
        } else {
            throw BluetoothDeviceException.EmptyDevicesList
        }
    }

    override suspend fun tryEnableBluetooth() {
        for (i in 0..TRY_BLUETOOTH_ENABLE_MAX_ATTEMPTS) {
            if (isBluetoothEnabled) {
                break
            } else {
                bluetoothAdapter?.enable()
                delay(TRY_BLUETOOTH_ENABLE_DELAY)
            }
        }

        if (!isBluetoothEnabled) throw BluetoothDeviceException.FailedToEnable
    }
}
