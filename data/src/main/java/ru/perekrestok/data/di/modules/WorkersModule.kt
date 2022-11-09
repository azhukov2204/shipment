package ru.perekrestok.data.di.modules

import org.koin.dsl.module
import ru.perekrestok.android.worker.SyncService
import ru.perekrestok.android.worker.SyncServiceImpl
import ru.perekrestok.data.worker.BluetoothScannerWorker

internal val workersModule = module {
    single<SyncService> {
        SyncServiceImpl(
            workers = listOf(
                get<BluetoothScannerWorker>()
            )
        )
    }
}
