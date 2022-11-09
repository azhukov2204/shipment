package ru.perekrestok.domain.exception

sealed class AdminModeException : Exception() {
    object PasswordEmpty : AdminModeException()
    object PasswordWrong : AdminModeException()
}
