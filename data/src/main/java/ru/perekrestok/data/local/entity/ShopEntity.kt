package ru.perekrestok.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_table")
internal data class ShopEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "sap_id")
    val sapId: String,
    @ColumnInfo(name = "shop_kpp")
    val shopKpp: String,
    @ColumnInfo(name = "shop_inn")
    val shopInn: String,
    @ColumnInfo(name = "host_name")
    val hostName: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double
)
