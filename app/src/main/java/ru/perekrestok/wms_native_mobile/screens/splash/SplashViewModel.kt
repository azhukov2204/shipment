package ru.perekrestok.wms_native_mobile.screens.splash

import kotlinx.coroutines.launch
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.interactor.InitAppInteractor
import ru.perekrestok.domain.interactor.ShopsInteractor
import ru.perekrestok.wms_native_mobile.activity.MainActivityRouter
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerScreen
import ru.perekrestok.wms_native_mobile.screens.shops.ShopsFragment
import ru.perekrestok.wms_native_mobile.screens.shops.ShopsScreen

class SplashViewModel(
    private val mainActivityRouter: MainActivityRouter,
    private val shopsInteractor: ShopsInteractor,
    private val initAppInteractor: InitAppInteractor
) : BaseViewModel<SplashViewState>() {

    override fun initialViewState(): SplashViewState = SplashViewState()

    override suspend fun reduce(event: Event, currentState: SplashViewState): SplashViewState = when (event) {
        is SplashUiEvent -> dispatchSplashUiEvent(event, currentState)
        else -> currentState
    }

    private fun dispatchSplashUiEvent(event: SplashUiEvent, currentState: SplashViewState): SplashViewState =
        when (event) {
            SplashUiEvent.OnPermissionsChecked -> currentState.also {
                handleOnPermissionsChecked()
            }
        }

    private fun handleOnPermissionsChecked() = viewModelScopeIO.launch {
        initAppInteractor.checkInitDate()

        val targetScreen = if (shopsInteractor.getCurrentShop() == null) {
            ShopsScreen(ShopsFragment.Companion.AfterChoseShopRouting.NEW_ROOT_SCREEN)
        } else {
            NavDrawerScreen
        }
        mainActivityRouter.newRootScreen(targetScreen)
    }
}
