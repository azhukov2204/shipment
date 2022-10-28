package ru.perekrestok.domain.repository

import ru.perekrestok.domain.entity.Shop

interface RemoteShopsRepository {
    suspend fun getShops(): List<Shop>
}

