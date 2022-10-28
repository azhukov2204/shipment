package ru.perekrestok.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_settings_table")
data class CustomSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "value")
    val value: String,
)