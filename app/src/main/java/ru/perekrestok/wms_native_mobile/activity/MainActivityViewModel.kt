package ru.perekrestok.wms_native_mobile.activity

import kotlinx.coroutines.launch
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.interactor.ScreenOrientationInteractor
import ru.perekrestok.wms_native_mobile.screens.splash.SplashScreen

class MainActivityViewModel(
    private val mainActivityRouter: MainActivityRouter,
    private val screenOrientationInteractor: ScreenOrientationInteractor
) : BaseViewModel<MainActivityViewState>() {

    init {
        viewModelScopeIO.launch {
            screenOrientationInteractor.getScreenOrientationFlow().collect { screenOrientation ->
                processDataEvent(MainActivityDataEvent.OnScreenOrientationReceiver(screenOrientation))

            }
        }
    }

    override fun initialViewState(): MainActivityViewState = MainActivityViewState()

    override suspend fun reduce(event: Event, currentState: MainActivityViewState): MainActivityViewState =
        when (event) {
            is LifecycleEvent -> dispatchLifecycleEvent(event, currentState)
            is MainActivityDataEvent -> dispatchMainActivityDataEvent(event, currentState)
            else -> currentState
        }

    private fun dispatchLifecycleEvent(
        event: LifecycleEvent,
        currentState: MainActivityViewState
    ): MainActivityViewState = when (event) {
        LifecycleEvent.OnLifecycleOwnerCreate -> handleOnLifecycleOwnerCreate(currentState)
        else -> currentState
    }

    private fun dispatchMainActivityDataEvent(
        event: MainActivityDataEvent,
        currentState: MainActivityViewState
    ): MainActivityViewState = when (event) {
        is MainActivityDataEvent.OnScreenOrientationReceiver -> currentState.copy(
            screenOrientation = event.screenOrientation
        )
    }

    private fun handleOnLifecycleOwnerCreate(currentState: MainActivityViewState): MainActivityViewState {
        return if (currentState.isAlreadyInitialized.not()) {
            mainActivityRouter.newRootScreen(SplashScreen)
            currentState.copy(isAlreadyInitialized = true)
        } else {
            currentState
        }
    }
}
