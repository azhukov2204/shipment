package ru.perekrestok.wms_native_mobile.di.modules

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.perekrestok.wms_native_mobile.activity.MainActivityViewModel
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerViewModel
import ru.perekrestok.wms_native_mobile.screens.search_device.SearchDeviceViewModel
import ru.perekrestok.wms_native_mobile.screens.settings.SettingsViewModel
import ru.perekrestok.wms_native_mobile.screens.shipment.ShipmentViewModel
import ru.perekrestok.wms_native_mobile.screens.shops.ShopsViewModel
import ru.perekrestok.wms_native_mobile.screens.splash.SplashViewModel

internal val viewModelModule = module {

    viewModel {
        MainActivityViewModel(
            mainActivityRouter = get(),
            screenOrientationInteractor = get()
        )
    }

    viewModel {
        SplashViewModel(
            mainActivityRouter = get(),
            shopsInteractor = get(),
            initAppInteractor = get()
        )
    }

    viewModel {
        NavDrawerViewModel(
            navDrawerRouter = get(),
            deviceInteractor = get(),
            stringResourceProvider = get(),
            settingsInteractor = get(),
            systemActionInteractor = get()
        )
    }
    viewModel {
        ShipmentViewModel(
            settingsInteractor = get(),
            deviceInteractor = get(),
            wireInteractor = get(),
            webViewOptionInteractor = get(),
            javaScriptCommandInteractor = get(),
            systemActionInteractor = get(),
            navDrawerRouter = get(),
            stringResourceProvider = get()
        )
    }

    viewModel { parameters ->
        ShopsViewModel(
            afterChoseShopNavigation = parameters.component1(),
            shopsInteractor = get(),
            router = get()
        )
    }

    viewModel {
        SettingsViewModel(
            navDrawerRouter = get(),
            mainActivityRouter = get(),
            stringResourceProvider = get(),
            settingsInteractor = get(),
            adminModeInteractor = get(),
            screenOrientationInteractor = get(),
            webViewOptionInteractor = get(),
            wireInteractor = get(),
            systemActionInteractor = get()
        )
    }

    viewModel { parameters ->
        SearchDeviceViewModel(
            deviceType = parameters.component1(),
            deviceInteractor = get(),
            stringResourceProvider = get(),
            mainActivityRouter = get(),
            settingsInteractor = get()
        )
    }
}
