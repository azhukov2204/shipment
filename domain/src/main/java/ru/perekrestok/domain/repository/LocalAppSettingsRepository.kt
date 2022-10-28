package ru.perekrestok.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.SettingKey

interface LocalAppSettingsRepository {
    suspend fun <T> saveSetting(appSetting: AppSetting<T>)
    suspend fun getSettings(): List<AppSetting<*>>
    fun getSettingsFlow(): Flow<List<AppSetting<*>>>
    suspend fun deleteSetting(settingKey: SettingKey)
    suspend fun saveRawSetting(key: String, value: String)
}
