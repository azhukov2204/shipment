package ru.perekrestok.wms_native_mobile.screens.splash

import ru.perekrestok.android.view_model.event.UiEvent

class SplashViewState

sealed interface SplashUiEvent : UiEvent {
    object OnPermissionsChecked : SplashUiEvent
}
