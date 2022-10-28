package ru.perekrestok.wms_native_mobile.screens.nav_drawer_container

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.jetbrains.annotations.NotNull
import ru.perekrestok.android.navigation.AppRouter
import ru.perekrestok.android.navigation.SupportAppNavigator
import ru.perekrestok.wms_native_mobile.di.DIQualifiers
import ru.perekrestok.wms_native_mobile.di.modules.NAV_DRAWER_QUALIFIER


object NavDrawerRouter : AppRouter(DIQualifiers.routerQualifier(NAV_DRAWER_QUALIFIER).value)

class NavDrawerNavigator(
    activity: @NotNull FragmentActivity,
    fragmentManager: @NotNull FragmentManager,
    containerId: Int,
) : SupportAppNavigator(activity, fragmentManager, containerId)
