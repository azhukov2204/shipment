package ru.perekrestok.data.remote.okhttp

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class HttpLogger(private val gson: Gson) : HttpLoggingInterceptor.Logger {

    companion object {
        private const val MAXIMUM_LOG_SIZE = 100000
    }

    override fun log(message: String) {

        /**
         *  Set to false to quickly disable formatting of JSON response
         */
        val isEnabled = true
        if (isEnabled) {
            if (message.startsWith("{") || message.startsWith("[")) {
                try {
                    val prettyPrintJson = gson.toJson(JsonParser.parseString(message))
                    Timber.d(prettyPrintJson)
                } catch (error: JsonSyntaxException) {
                    Timber.e(error)
                    Timber.d(message)
                }
            } else if (message.length < MAXIMUM_LOG_SIZE) {
                Timber.d(message)
                return
            }
        } else {
            HttpLoggingInterceptor.Logger.DEFAULT.log(message)
        }
    }
}
