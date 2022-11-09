package ru.perekrestok.wms_native_mobile.screens.shipment.web_view

import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import timber.log.Timber

class ShopsChromeWebViewClient(
    private val pageLoadProgressListener: (progress: Int) -> Unit
) : WebChromeClient() {

    companion object {
        private const val MESSAGE_TAG = "chromium |console/error"
        private const val MESSAGE_FORMAT = "%s @ %d: %s"
        private const val PROGRESS_MAX_VALUE = 100
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        val message = String.format(
            MESSAGE_FORMAT,
            consoleMessage?.message().orEmpty(),
            consoleMessage?.lineNumber() ?: 0,
            consoleMessage?.sourceId().orEmpty()
        )

        return if (consoleMessage?.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            Timber.e(MESSAGE_TAG, message)
            true
        } else {
            Timber.d(MESSAGE_TAG, message)
            super.onConsoleMessage(consoleMessage)
        }
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        pageLoadProgressListener(newProgress)
        if (newProgress == PROGRESS_MAX_VALUE) {
            view?.clearHistory()
        }
    }
}
