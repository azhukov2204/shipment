package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.CustomSetting
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.domain.repository.LocalCustomSettingsRepository

interface SettingsInteractor {
    fun getAppSettingsFlow(): Flow<List<AppSetting<*>>>
    suspend fun getAppSettings(): List<AppSetting<*>>
    suspend fun setSettingValueByKey(key: String, value: String)
    fun getCustomSettingsFlow(): Flow<List<CustomSetting>>
    suspend fun saveCustomSetting(key: String, type: String, value: String)
}

class SettingsInteractorImpl(
    private val localAppSettingsRepository: LocalAppSettingsRepository,
    private val localCustomSettingsRepository: LocalCustomSettingsRepository
) : SettingsInteractor {
    override fun getAppSettingsFlow(): Flow<List<AppSetting<*>>> {
        return localAppSettingsRepository.getSettingsFlow()
    }

    override suspend fun setSettingValueByKey(key: String, value: String) {
        localAppSettingsRepository.saveRawSetting(key, value)
    }

    override fun getCustomSettingsFlow(): Flow<List<CustomSetting>> {
        return localCustomSettingsRepository.getSettingsFlow()
    }

    override suspend fun saveCustomSetting(key: String, type: String, value: String) {
        localCustomSettingsRepository.saveSetting(
            CustomSetting(
                key = key,
                type = type,
                value = value
            )
        )
    }

    override suspend fun getAppSettings(): List<AppSetting<*>> {
        return localAppSettingsRepository.getSettings()
    }
}