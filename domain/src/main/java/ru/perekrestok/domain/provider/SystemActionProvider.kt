package ru.perekrestok.domain.provider

interface SystemActionProvider {
    fun getAppName(): String
    fun getAppCode(): Long
    fun getDateInstallApp(): String
    fun getImei(): String
    suspend fun clearCookies()
    fun doVibro(time: Long)
    fun restartApp()
}