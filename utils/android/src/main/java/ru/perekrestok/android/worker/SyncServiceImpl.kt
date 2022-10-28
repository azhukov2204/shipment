package ru.perekrestok.android.worker

import androidx.lifecycle.LifecycleOwner

class SyncServiceImpl(
    private val workers: List<BaseSyncWorker<out Any>>
) : SyncService {

    override fun onResume(owner: LifecycleOwner) {
        start()
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }

    private fun start() {
        workers.forEach { it.start() }
    }

    private fun stop() {
        workers.forEach { it.stop() }
    }
}
