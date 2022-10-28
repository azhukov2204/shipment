package ru.perekrestok.data.local.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.perekrestok.data.local.dao.ShopsDao
import ru.perekrestok.data.local.entity.ShopEntity
import ru.perekrestok.domain.entity.MapPosition
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.repository.LocalShopsRepository

internal class LocalShopsRepositoryImpl(
    private val shopsDao: ShopsDao
) : LocalShopsRepository {

    override suspend fun saveShops(shops: List<Shop>) {
        shopsDao.saveShops(shops.toEntities())
    }

    override fun getShopsFlow(): Flow<List<Shop>> {
        return shopsDao.getShopsFlow().map { shopEntities ->
            shopEntities.toDomain()
        }
    }

    private fun List<Shop>.toEntities(): List<ShopEntity> =
        map { shop ->
            ShopEntity(
                id = shop.id,
                name = shop.name,
                sapId = shop.sapId,
                shopKpp = shop.kpp,
                shopInn = shop.inn,
                hostName = shop.hostName,
                latitude = shop.mapPosition.latitude,
                longitude = shop.mapPosition.longitude,
            )
        }

    private fun ShopEntity.toDomain(): Shop = Shop(
        id = id,
        name = name,
        sapId = sapId,
        kpp = shopKpp,
        inn = shopInn,
        hostName = hostName,
        mapPosition = MapPosition(
            latitude = latitude,
            longitude = longitude
        ),
    )

    private fun List<ShopEntity>.toDomain(): List<Shop> =
        map { shopEntity -> shopEntity.toDomain() }
}
