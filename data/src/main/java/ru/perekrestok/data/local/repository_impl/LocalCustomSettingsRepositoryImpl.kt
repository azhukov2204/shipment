package ru.perekrestok.data.local.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.perekrestok.data.local.dao.CustomSettingsDao
import ru.perekrestok.data.local.entity.CustomSettingEntity
import ru.perekrestok.domain.entity.CustomSetting
import ru.perekrestok.domain.repository.LocalCustomSettingsRepository

class LocalCustomSettingsRepositoryImpl(
    private val customSettingsDao: CustomSettingsDao
) : LocalCustomSettingsRepository {

    override fun getSettingsFlow(): Flow<List<CustomSetting>> {
        return customSettingsDao.getSettingsFlow().map { customSettingEntities ->
            customSettingEntities.toDomain()
        }
    }

    override suspend fun saveSetting(setting: CustomSetting) {
        customSettingsDao.saveSetting(setting.toEntity())
    }

    private fun List<CustomSettingEntity>.toDomain(): List<CustomSetting> {
        return map { customSettingEntity ->
            CustomSetting(
                key = customSettingEntity.key,
                type = customSettingEntity.type,
                value = customSettingEntity.value
            )
        }
    }

    private fun CustomSetting.toEntity(): CustomSettingEntity {
        return CustomSettingEntity(
            key = key,
            type = type,
            value = value
        )
    }
}


