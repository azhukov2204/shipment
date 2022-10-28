package ru.perekrestok.wms_native_mobile.di.modules

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import org.koin.dsl.module
import ru.perekrestok.wms_native_mobile.activity.MainActivityRouter
import ru.perekrestok.wms_native_mobile.di.DIQualifiers
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerRouter

const val MAIN_ACTIVITY_QUALIFIER = "main_activity"
const val NAV_DRAWER_QUALIFIER = "nav_drawer"

internal val mainActivityModule = module {

    val ciceroneQualifier = DIQualifiers.ciceroneQualifier(MAIN_ACTIVITY_QUALIFIER)
    val navigationHolderQualifier = DIQualifiers.navigationHolderQualifier(MAIN_ACTIVITY_QUALIFIER)

    single<Cicerone<MainActivityRouter>>(ciceroneQualifier) {
        Cicerone.create(MainActivityRouter)
    }

    single<NavigatorHolder>(navigationHolderQualifier) {
        get<Cicerone<MainActivityRouter>>(ciceroneQualifier).getNavigatorHolder()
    }

    single<MainActivityRouter> {
        get<Cicerone<MainActivityRouter>>(ciceroneQualifier).router
    }

    val ciceroneNavDrawerQualifier = DIQualifiers.ciceroneQualifier(NAV_DRAWER_QUALIFIER)
    val navigationNavDrawerHolderQualifier = DIQualifiers.navigationHolderQualifier(NAV_DRAWER_QUALIFIER)

    single<Cicerone<NavDrawerRouter>>(ciceroneNavDrawerQualifier) {
        Cicerone.create(NavDrawerRouter)
    }

    single<NavigatorHolder>(navigationNavDrawerHolderQualifier) {
        get<Cicerone<NavDrawerRouter>>(ciceroneNavDrawerQualifier).getNavigatorHolder()
    }

    single<NavDrawerRouter> {
        get<Cicerone<NavDrawerRouter>>(ciceroneNavDrawerQualifier).router
    }
}