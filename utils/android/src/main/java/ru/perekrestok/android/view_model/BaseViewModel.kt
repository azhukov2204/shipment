package ru.perekrestok.android.view_model

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.perekrestok.android.view_model.event.DataEvent
import ru.perekrestok.android.view_model.event.ErrorEvent
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.android.view_model.event.UiEvent

abstract class BaseViewModel<VIEW_STATE> : ViewModel() {

    val viewModelScopeIO: CoroutineScope = viewModelScope + Dispatchers.IO

    private val mutex = Mutex()

    internal val viewState: MutableLiveData<VIEW_STATE> by lazy {
        ViewStateLiveData(
            initialViewState = initialViewState(),
            lifecycleObserver = lifecycleObserver
        )
    }

    internal val singleEvent: SingleLiveEvent<SingleEvent> by lazy(::SingleLiveEvent)

    private val uiVisibilityChanges = { event: LifecycleEvent ->
        processLifecycleEvent(event)
        isUiVisible = event is LifecycleEvent.OnLifecycleOwnerResume
    }

    private var isUiVisible = false

    protected abstract fun initialViewState(): VIEW_STATE

    protected abstract suspend fun reduce(event: Event, currentState: VIEW_STATE): VIEW_STATE?

    private val lifecycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerPause)
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerResume)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerStart)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerStop)
        }

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerCreate)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)

            uiVisibilityChanges(LifecycleEvent.OnLifecycleOwnerDestroy)
        }
    }

    fun processUiEvent(event: UiEvent) {
        updateState(event)
    }

    protected fun processDataEvent(event: DataEvent) {
        updateState(event)
    }

    protected fun processErrorEvent(event: ErrorEvent) {
        updateState(event)
    }

    protected fun processSingleEvent(event: SingleEvent) {
        singleEvent.postValue(event)
    }

    protected open fun dispatchBackEvent(router: Router, currentState: VIEW_STATE): VIEW_STATE {
        router.exit()
        return currentState
    }

    private fun updateState(event: Event) {
        viewModelScopeIO.launch {
            mutex.withLock {
                val newViewState = reduce(event, viewState.value ?: initialViewState())
                compareNewStateWithCurrentAndUpdate(newViewState, event)
            }
        }
    }

    private suspend fun compareNewStateWithCurrentAndUpdate(
        newViewState: VIEW_STATE?,
        event: Event
    ) {
        if (newViewState != null && newViewState != viewState.value) {
            val oldViewState = viewState.value
            withContext(Dispatchers.Main) { viewState.value = newViewState }
            onAfterStateChanged(oldViewState!!, newViewState, event)
        }
    }

    protected open suspend fun onAfterStateChanged(
        oldViewState: VIEW_STATE,
        newViewState: VIEW_STATE,
        event: Event
    ) {
    }

    private fun processLifecycleEvent(event: LifecycleEvent) {
        updateState(event)
    }

    /**
     * [OnLifecycleOwnerResume] эквивалент onResume у фрагмента
     * [OnLifecycleOwnerPause] эквивалент onPause у фрагмента
     * [OnLifecycleOwnerStart] эквивалент onStop у фрагмента
     * [OnLifecycleOwnerStop] эквивалент onStart у фрагмента
     * [OnLifecycleOwnerCreate] эквивалент onCreate у фрагмента
     * [OnLifecycleOwnerDestroy] эквивалент onDestroy у фрагмента
     */
    protected sealed class LifecycleEvent : Event {
        object OnLifecycleOwnerResume : LifecycleEvent()
        object OnLifecycleOwnerPause : LifecycleEvent()
        object OnLifecycleOwnerStart : LifecycleEvent()
        object OnLifecycleOwnerStop : LifecycleEvent()
        object OnLifecycleOwnerCreate : LifecycleEvent()
        object OnLifecycleOwnerDestroy : LifecycleEvent()
    }

    private class ViewStateLiveData<VIEW_STATE>(
        initialViewState: VIEW_STATE,
        private val lifecycleObserver: LifecycleObserver
    ) : MutableLiveData<VIEW_STATE>(initialViewState) {

        private var lifecycle: Lifecycle? = null
            set(value) {
                if (value != null) {
                    field = value
                    value.addObserver(lifecycleObserver)
                } else {
                    field?.removeObserver(lifecycleObserver)
                    field = value
                }
            }

        override fun observe(owner: LifecycleOwner, observer: Observer<in VIEW_STATE>) {
            super.observe(owner, observer)
            lifecycle = owner.lifecycle
        }

        override fun removeObserver(observer: Observer<in VIEW_STATE>) {
            super.removeObserver(observer)
            if (!hasActiveObservers()) lifecycle = null
        }

        override fun removeObservers(owner: LifecycleOwner) {
            super.removeObservers(owner)
            lifecycle = null
        }
    }
}
