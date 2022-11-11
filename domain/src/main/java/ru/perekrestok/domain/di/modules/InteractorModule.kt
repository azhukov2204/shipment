package ru.perekrestok.domain.di.modules

import org.koin.dsl.binds
import org.koin.dsl.module
import ru.perekrestok.domain.interactor.AdminModeInteractor
import ru.perekrestok.domain.interactor.AdminModeInteractorImpl
import ru.perekrestok.domain.interactor.DeviceInteractor
import ru.perekrestok.domain.interactor.DeviceInteractorImpl
import ru.perekrestok.domain.interactor.InitAppInteractor
import ru.perekrestok.domain.interactor.InitAppInteractorImpl
import ru.perekrestok.domain.interactor.JavaScriptCommandInteractor
import ru.perekrestok.domain.interactor.JavaScriptCommandInteractorImpl
import ru.perekrestok.domain.interactor.PrinterInteractor
import ru.perekrestok.domain.interactor.ScannerInteractor
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

    single {
        DeviceInteractorImpl(
            bluetoothDeviceProvider = get(),
            settingsRepository = get(),
            bluetoothScannerProvider = get(),
            bluetoothPrinterProvider = get(),
            localAppSettingsRepository = get()
        )
    } binds arrayOf(DeviceInteractor::class, ScannerInteractor::class, PrinterInteractor::class)

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

    single<JavaScriptCommandInteractor> {
        JavaScriptCommandInteractorImpl(
            renderJSBarcodeProvider = get()
        )
    }
}
