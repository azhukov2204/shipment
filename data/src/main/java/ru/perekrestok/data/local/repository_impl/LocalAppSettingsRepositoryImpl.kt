package ru.perekrestok.data.local.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import ru.perekrestok.data.local.dao.AppSettingsDao
import ru.perekrestok.data.local.entity.AppSettingEntity
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.Device
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.entity.SettingType
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import timber.log.Timber

class LocalAppSettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao
) : LocalAppSettingsRepository {

    override suspend fun <T> saveSetting(appSetting: AppSetting<T>) {
        appSettingsDao.saveSetting(
            appSetting.toEntity()
        )
    }

    override fun getSettingsFlow(): Flow<List<AppSetting<*>>> {
        return appSettingsDao.getSettingsFlow().map { settingEntities ->
            settingEntities.toDomain()
        }.distinctUntilChanged()
    }

    override suspend fun getSettings(): List<AppSetting<*>> {
        return appSettingsDao.getSettings().toDomain()
    }

    override suspend fun deleteSetting(settingKey: SettingKey) {
        appSettingsDao.deleteSetting(settingKey.keyName)
    }

    override suspend fun saveRawSetting(key: String, value: String) {
        SettingKey.getByKeyName(key)?.let { settingKey ->
            AppSettingEntity(
                key = settingKey.keyName,
                type = settingKey.settingType.typeName,
                value = value
            )
        }
            ?.toDomain() // двойное преобразование - чтоб не реализовывать дополнительной проверки на соответствие типа
            ?.toEntity()?.let { setting ->
                appSettingsDao.saveSetting(setting)
            }
    }

    private fun AppSetting<*>.toEntity(): AppSettingEntity {
        val value = when (key.settingType) {
            SettingType.STRING -> (value as String)
            SettingType.INT -> (value as Int).toString()
            SettingType.FLOAT -> (value as Float).toString()
            SettingType.LONG -> (value as Long).toString()
            SettingType.BOOLEAN -> (value as Boolean).toString()
            SettingType.SHOP -> Json.encodeToString(value as Shop)
            SettingType.DEVICE -> Json.encodeToString(value as Device)
        }

        return AppSettingEntity(
            key = key.keyName,
            type = key.settingType.typeName,
            value = value
        )
    }

    private fun List<AppSettingEntity>.toDomain(): List<AppSetting<*>> = mapNotNull { settingEntity ->
        settingEntity.toDomain()
    }

    private fun AppSettingEntity.toDomain(): AppSetting<*>? {
        return try {
            val settingKey = SettingKey.getByKeyName(key)

            val settingValue: Any? = when (SettingType.getByTypeName(type)) {
                SettingType.STRING -> value
                SettingType.INT -> value.toIntOrNull()
                SettingType.FLOAT -> value.toFloatOrNull()
                SettingType.LONG -> value.toLongOrNull()
                SettingType.BOOLEAN -> value.toBooleanStrictOrNull()
                SettingType.SHOP -> Json.decodeFromString<Shop>(serializer(), value)
                SettingType.DEVICE -> Json.decodeFromString<Device>(serializer(), value)
            }

            if (settingKey != null && settingValue != null) {
                AppSetting(
                    key = settingKey,
                    value = settingValue
                )
            } else {
                null
            }
        } catch (error: Throwable) {
            Timber.e(error)
            null
        }
    }
}
