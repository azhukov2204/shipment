package ru.perekrestok.domain.exception

sealed class PrinterException : Exception() {
    object NoPrinter : PrinterException()
    data class CouldNotOpenConnection(override val message: String) : PrinterException()
}
