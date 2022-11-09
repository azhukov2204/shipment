package ru.perekrestok.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.CustomSetting

interface LocalCustomSettingsRepository {
    fun getSettingsFlow(): Flow<List<CustomSetting>>
    suspend fun saveSetting(setting: CustomSetting)
}
