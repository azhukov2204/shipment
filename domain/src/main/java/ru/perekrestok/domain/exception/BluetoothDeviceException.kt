package ru.perekrestok.domain.exception

sealed class BluetoothDeviceException : Exception() {
    object FailedToEnable : BluetoothDeviceException()
    object EmptyDevicesList : BluetoothDeviceException()
}
