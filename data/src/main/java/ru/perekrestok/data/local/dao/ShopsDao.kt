package ru.perekrestok.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.perekrestok.data.local.entity.ShopEntity

@Dao
internal interface ShopsDao {

    @Transaction
    suspend fun saveShops(shops: List<ShopEntity>) {
        deleteAll()
        insertAll(shops)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<ShopEntity>)

    @Query("DELETE FROM shop_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM shop_table")
    fun getShopsFlow(): Flow<List<ShopEntity>>
}
