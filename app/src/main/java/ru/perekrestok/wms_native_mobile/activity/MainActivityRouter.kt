package ru.perekrestok.wms_native_mobile.activity

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.jetbrains.annotations.NotNull
import ru.perekrestok.android.navigation.AppRouter
import ru.perekrestok.android.navigation.SupportAppNavigator
import ru.perekrestok.wms_native_mobile.di.DIQualifiers
import ru.perekrestok.wms_native_mobile.di.modules.MAIN_ACTIVITY_QUALIFIER

object MainActivityRouter : AppRouter(DIQualifiers.routerQualifier(MAIN_ACTIVITY_QUALIFIER).value)

class MainActivityNavigator(
    activity: @NotNull FragmentActivity,
    fragmentManager: @NotNull FragmentManager,
    containerId: Int,
) : SupportAppNavigator(activity, fragmentManager, containerId)
