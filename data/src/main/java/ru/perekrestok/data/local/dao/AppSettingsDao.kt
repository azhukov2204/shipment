package ru.perekrestok.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.perekrestok.data.local.entity.AppSettingEntity

@Dao
interface AppSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSettingEntity)

    @Query("SELECT * FROM app_settings_table")
    fun getSettingsFlow(): Flow<List<AppSettingEntity>>

    @Query("SELECT * FROM app_settings_table")
    suspend fun getSettings(): List<AppSettingEntity>

    @Query("DELETE FROM app_settings_table WHERE `key` = :key")
    suspend fun deleteSetting(key: String)
}
