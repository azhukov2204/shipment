package ru.perekrestok.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.perekrestok.data.local.entity.CustomSettingEntity

@Dao
interface CustomSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: CustomSettingEntity)

    @Query("SELECT * FROM custom_settings_table")
    fun getSettingsFlow(): Flow<List<CustomSettingEntity>>
}