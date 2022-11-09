package ru.perekrestok.kotlin

import java.text.SimpleDateFormat
import java.util.Locale

val currentTimeMillis: Long get() = System.currentTimeMillis()

fun Long.toStringDate(format: String = "dd.MM.yyyy HH:mm"): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (ignore: Exception) {
        StringPatterns.EMPTY_SYMBOL
    }
}
