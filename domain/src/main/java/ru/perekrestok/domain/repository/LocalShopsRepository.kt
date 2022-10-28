package ru.perekrestok.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.Shop

interface LocalShopsRepository {
    suspend fun saveShops(shops: List<Shop>)
    fun getShopsFlow(): Flow<List<Shop>>
}
