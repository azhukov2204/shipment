package ru.perekrestok.domain.interactor

import ru.perekrestok.domain.provider.SystemActionProvider

interface SystemActionInteractor {
    fun getAppName(): String
    fun getAppCode(): Long
    fun getDateInstallApp(): String
    fun getImei(): String
    suspend fun clearCookies()
    fun doVibro(time: Long)
    fun restartApp()
}

class SystemActionInteractorImpl(
    private val systemActionProvider: SystemActionProvider
) : SystemActionInteractor {

    override fun getAppName(): String {
        return systemActionProvider.getAppName()
    }

    override fun getAppCode(): Long {
        return systemActionProvider.getAppCode()
    }

    override fun getDateInstallApp(): String {
        return systemActionProvider.getDateInstallApp()
    }

    override fun getImei(): String {
        return systemActionProvider.getImei()
    }

    override suspend fun clearCookies() {
        systemActionProvider.clearCookies()
    }

    override fun doVibro(time: Long) {
        systemActionProvider.doVibro(time)
    }

    override fun restartApp() {
        systemActionProvider.restartApp()
    }
}
