package ru.perekrestok.domain.interactor

import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.repository.LocalAppSettingsRepository

interface WebViewOptionInteractor {
    suspend fun setIsAllowedCookies(isAllowed: Boolean)
    suspend fun setIsAllowedCache(isAllowed: Boolean)
    suspend fun saveCacheMode(cacheMode: Int)
    suspend fun saveCustomEndpoint(url: String)
    suspend fun removeCustomEndpoint()
    suspend fun saveScanMode(scanModeId: Int)
}

class WebViewOptionInteractorImpl(
    private val localAppSettingsRepository: LocalAppSettingsRepository
) : WebViewOptionInteractor {

    override suspend fun setIsAllowedCookies(isAllowed: Boolean) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.IS_COOKIES_ALLOWED,
                value = isAllowed
            )
        )
    }

    override suspend fun setIsAllowedCache(isAllowed: Boolean) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.IS_CACHE_ALLOWED,
                value = isAllowed
            )
        )
    }

    override suspend fun saveCacheMode(cacheMode: Int) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.WEB_VIEW_CACHE_MODE,
                value = cacheMode
            )
        )
    }

    override suspend fun saveCustomEndpoint(url: String) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.CUSTOM_ENDPOINT,
                value = url
            )
        )
    }

    override suspend fun removeCustomEndpoint() {
        localAppSettingsRepository.deleteSetting(SettingKey.CUSTOM_ENDPOINT)
    }

    override suspend fun saveScanMode(scanModeId: Int) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SCAN_MODE_ID,
                value = scanModeId
            )
        )
    }
}
