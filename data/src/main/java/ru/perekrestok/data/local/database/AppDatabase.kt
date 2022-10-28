package ru.perekrestok.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.perekrestok.data.local.dao.AppSettingsDao
import ru.perekrestok.data.local.dao.CustomSettingsDao
import ru.perekrestok.data.local.dao.ShopsDao
import ru.perekrestok.data.local.entity.AppSettingEntity
import ru.perekrestok.data.local.entity.CustomSettingEntity
import ru.perekrestok.data.local.entity.ShopEntity

@Database(
    entities = [
        ShopEntity::class,
        AppSettingEntity::class,
        CustomSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "wms_native_database.db"
    }

    abstract fun shopsDao(): ShopsDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun customSettingsDao(): CustomSettingsDao
}