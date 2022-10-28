package ru.perekrestok.data.worker

import ru.perekrestok.android.worker.BaseSyncWorker
import ru.perekrestok.domain.entity.Device

internal abstract class BluetoothScannerWorker : BaseSyncWorker<Device>()
