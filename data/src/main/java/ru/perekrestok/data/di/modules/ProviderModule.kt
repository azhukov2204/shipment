package ru.perekrestok.data.di.modules

import org.koin.dsl.binds
import org.koin.dsl.module
import ru.perekrestok.data.provider_impl.BluetoothDeviceProviderImpl
import ru.perekrestok.data.provider_impl.BluetoothPrinterProviderImpl
import ru.perekrestok.data.provider_impl.BluetoothScannerProviderImpl
import ru.perekrestok.data.provider_impl.RenderJSBarcodeProviderImpl
import ru.perekrestok.data.provider_impl.StringResourceProviderImpl
import ru.perekrestok.data.provider_impl.SystemActionProviderImpl
import ru.perekrestok.data.worker.BluetoothScannerWorker
import ru.perekrestok.domain.provider.BluetoothDeviceProvider
import ru.perekrestok.domain.provider.BluetoothPrinterProvider
import ru.perekrestok.domain.provider.BluetoothScannerProvider
import ru.perekrestok.domain.provider.StringResourceProvider
import ru.perekrestok.domain.provider.SystemActionProvider
import ru.perekrestok.domain.repository.RenderJSBarcodeProvider

internal val providerModule = module {
    single<StringResourceProvider> {
        StringResourceProviderImpl(
            context = get()
        )
    }

    single<BluetoothDeviceProvider> {
        BluetoothDeviceProviderImpl(
            context = get()
        )
    }

    single {
        BluetoothScannerProviderImpl(
            context = get(),
            bluetoothDeviceProvider = get(),
            settingsRepository = get()
        )
    } binds arrayOf(BluetoothScannerProvider::class, BluetoothScannerWorker::class)

    single<BluetoothPrinterProvider> {
        BluetoothPrinterProviderImpl(
            context = get(),
            bluetoothDeviceProvider = get(),
            settingsRepository = get()
        )
    }

    single<SystemActionProvider> { SystemActionProviderImpl(context = get()) }

    single<RenderJSBarcodeProvider> { RenderJSBarcodeProviderImpl() }
}
