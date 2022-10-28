package ru.perekrestok.android.worker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Используется для запуска/остановки периодического запроса
 */
abstract class BaseSyncWorker<T> : SyncWorker {

    private var job: Job? = null
    protected val scope = CoroutineScope(Dispatchers.IO)
    protected var data: T? = null
        private set

    protected abstract suspend fun sync()
    protected abstract suspend fun stopSync()
    protected abstract suspend fun requestDelayMillis(): Long

    fun putData(data: T, withStartSync: Boolean = true) {
        this.data = data
        if (withStartSync) {
            startSync()
        }
    }

    fun removeData() {
        data = null
        stop()
    }

    private fun canSync(): Boolean = data != null

    override fun start() {
        startSync()
    }

    override fun stop() {
        scope.launch { stopSync() }
        job?.cancel()
    }

    private fun startSync() {
        if (job?.isActive == true) {
            stop()
        }
        job = scope.launch(Dispatchers.Default) {
            while (isActive) {
                if (canSync()) {
                    sync()
                }
                delay(requestDelayMillis())
            }
        }
    }
}
