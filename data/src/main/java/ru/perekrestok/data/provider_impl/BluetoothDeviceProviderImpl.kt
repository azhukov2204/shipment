package ru.perekrestok.data.provider_impl

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.perekrestok.android.extension.arePermissionsGranted
import ru.perekrestok.android.extension.bluetoothManager
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.exception.BluetoothDeviceException
import ru.perekrestok.domain.provider.BluetoothDeviceProvider
import ru.perekrestok.kotlin.asSet

internal class BluetoothDeviceProviderImpl(
    private val context: Context
) : BluetoothDeviceProvider {

    companion object {
        private const val TRY_BLUETOOTH_ENABLE_MAX_ATTEMPTS = 10
        private const val TRY_BLUETOOTH_ENABLE_DELAY = 1000L
        private const val FLOW_EXTRA_BUFFER_CAPACITY = 1
    }

    private val bluetoothAdapter: BluetoothAdapter?
        get() = context.bluetoothManager.adapter

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val _searchDevicesMutableFlow: MutableSharedFlow<Set<Device>> =
        MutableSharedFlow(extraBufferCapacity = FLOW_EXTRA_BUFFER_CAPACITY)

    override val searchDevicesFlow: Flow<Set<Device>> = _searchDevicesMutableFlow.asSharedFlow()

    private val searchDeviceReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        val deviseSet = device?.takeIf { it.name != null }
                            ?.let { device.toDomain() }
                            ?.asSet()
                        deviseSet?.let { _searchDevicesMutableFlow.tryEmit(deviseSet) }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        restartSearchDevices()
                    }
                }
            }
        }

    override suspend fun startSearchDevices() {
        checkSearchDevicesPermission()
        tryEnableBluetooth()
        val bondedDevices = bluetoothAdapter?.bondedDevices?.map { bluetoothDevice ->
            bluetoothDevice.toDomain()
        }?.toSet()
        bondedDevices?.let { _searchDevicesMutableFlow.emit(bondedDevices) }

        val filterFound = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val filterDiscoveryFinished = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        context.registerReceiver(searchDeviceReceiver, filterFound)
        context.registerReceiver(searchDeviceReceiver, filterDiscoveryFinished)
        bluetoothAdapter?.startDiscovery()
    }

    fun restartSearchDevices() {
        bluetoothAdapter?.startDiscovery()
    }

    override suspend fun stopSearchDevices() {
        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(searchDeviceReceiver)
    }

    @Suppress("UnusedPrivateMember")
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

    private fun checkSearchDevicesPermission() {
        val isGranted = context.arePermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        if (isGranted.not()) {
            throw BluetoothDeviceException.SearchDeviceNotGranted
        }
    }

    private fun BluetoothDevice.toDomain(): Device {
        return Device(
            name = name,
            address = address
        )
    }
}
