package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getCurrentShop
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.domain.repository.LocalShopsRepository
import ru.perekrestok.domain.repository.RemoteShopsRepository


interface ShopsInteractor {
    suspend fun obtainShops(): Result<Unit>
    fun getCachedShopsFlow(): Flow<List<Shop>>
    suspend fun setCurrentShop(shop: Shop): Result<Unit>
    suspend fun getCurrentShop(): Shop?
    fun getCurrentShopFlow(): Flow<Shop>
}

class ShopsInteractorImpl(
    private val remoteShopsRepository: RemoteShopsRepository,
    private val localShopsRepository: LocalShopsRepository,
    private val settingsRepository: LocalAppSettingsRepository
) : ShopsInteractor {

    override suspend fun obtainShops(): Result<Unit> = runCatching {
        val shops = remoteShopsRepository.getShops()
        localShopsRepository.saveShops(shops)
    }

    override fun getCachedShopsFlow(): Flow<List<Shop>> {
        return localShopsRepository.getShopsFlow()
    }

    override suspend fun setCurrentShop(shop: Shop): Result<Unit> = runCatching {
        settingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SELECTED_SHOP,
                value = shop
            )
        )
    }

    override suspend fun getCurrentShop(): Shop? {
        return settingsRepository.getSettings().getCurrentShop()
    }

    override fun getCurrentShopFlow(): Flow<Shop> {
        return settingsRepository.getSettingsFlow().mapNotNull { settings ->
            settings.getCurrentShop()
        }
    }
}
