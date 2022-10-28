package ru.perekrestok.wms_native_mobile.activity

import android.content.pm.ActivityInfo
import ru.perekrestok.android.view_model.event.DataEvent

data class MainActivityViewState(
    val isAlreadyInitialized: Boolean = false,
    val screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
)

sealed interface MainActivityDataEvent : DataEvent {
    data class OnScreenOrientationReceiver(val screenOrientation: Int) : MainActivityDataEvent
}
