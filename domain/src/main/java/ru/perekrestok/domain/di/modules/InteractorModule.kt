package ru.perekrestok.domain.di.modules

import org.koin.dsl.module
import ru.perekrestok.domain.interactor.AdminModeInteractor
import ru.perekrestok.domain.interactor.AdminModeInteractorImpl
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.DeviceInteractorImpl
import ru.perekrestok.domain.interactor.InitAppInteractor
import ru.perekrestok.domain.interactor.InitAppInteractorImpl
import ru.perekrestok.domain.interactor.ScannerInteractor
import ru.perekrestok.domain.interactor.ScannerInteractorImpl
import ru.perekrestok.domain.interactor.ScreenOrientationInteractor
import ru.perekrestok.domain.interactor.ScreenOrientationInteractorImpl
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SettingsInteractorImpl
import ru.perekrestok.domain.interactor.ShopsInteractor
import ru.perekrestok.domain.interactor.ShopsInteractorImpl
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractorImpl
import ru.perekrestok.domain.interactor.WebViewOptionInteractor
import ru.perekrestok.domain.interactor.WebViewOptionInteractorImpl
import ru.perekrestok.domain.interactor.WireInteractor
import ru.perekrestok.domain.interactor.WireInteractorImpl

internal val interactorModule = module {

    single<ShopsInteractor> {
        ShopsInteractorImpl(
            remoteShopsRepository = get(),
            localShopsRepository = get(),
            settingsRepository = get()
        )
    }

    single<AdminModeInteractor> {
        AdminModeInteractorImpl(
            localAppSettingsRepository = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            localAppSettingsRepository = get(),
            localCustomSettingsRepository = get()
        )
    }

    single<DeviceInteractor> {
        DeviceInteractorImpl(
            bluetoothDeviceProvider = get(),
            settingsRepository = get(),
            bluetoothScannerProvider = get(),
            bluetoothPrinterProvider = get()
        )
    }

    single<ScreenOrientationInteractor> {
        ScreenOrientationInteractorImpl(
            settingsRepository = get()
        )
    }

    single<WebViewOptionInteractor> {
        WebViewOptionInteractorImpl(
            localAppSettingsRepository = get()
        )
    }

    single<SystemActionInteractor> { SystemActionInteractorImpl(systemActionProvider = get()) }

    single<InitAppInteractor> { InitAppInteractorImpl(localAppSettingsRepository = get()) }

    single<WireInteractor> { WireInteractorImpl() }

    single<ScannerInteractor> {
        ScannerInteractorImpl(
            localAppSettingsRepository = get(),
            bluetoothScannerProvider = get(),
            renderJSBarcodeProvider = get()
        )
    }
}
