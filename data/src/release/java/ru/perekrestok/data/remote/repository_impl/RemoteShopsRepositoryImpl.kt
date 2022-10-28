package ru.perekrestok.data.remote.repository_impl

import ru.perekrestok.data.remote.api.ShopRemote
import ru.perekrestok.data.remote.api.ShopsApi
import ru.perekrestok.domain.entity.AppSetting.Companion.getCurrentShop
import ru.perekrestok.domain.entity.AppSetting.Companion.getDateInitApp
import ru.perekrestok.domain.entity.MapPosition
import ru.perekrestok.domain.entity.Shop
import ru.perekrestok.domain.interactor.SettingsInteractor
import ru.perekrestok.domain.interactor.SystemActionInteractor
import ru.perekrestok.domain.repository.RemoteShopsRepository

/**
 * Это релизный вариант
 */
internal class RemoteShopsRepositoryImpl(
    private val shopsApi: ShopsApi,
    private val systemActionInteractor: SystemActionInteractor,
    private val settingsInteractor: SettingsInteractor
) : RemoteShopsRepository {

    companion object {
        // по shop_id = 2455 начали приходить пустые координаты. Поставил дефолтные значения на этот случай
        private const val DEFAULT_SHOP_LATITUDE: Double = 0.0
        private const val DEFAULT_SHOP_LONGITUDE: Double = 0.0
        private const val APP_NAME = "WMS-NATIVE-APPLICATION"
    }

    override suspend fun getShops(): List<Shop> {
        val appCode = systemActionInteractor.getAppCode().toString()
        val appVersion = systemActionInteractor.getAppName()
        val dateInstall = systemActionInteractor.getDateInstallApp()
        val imei = systemActionInteractor.getImei()
        val settings = settingsInteractor.getAppSettings()
        val dateInit = settings.getDateInitApp().orEmpty()
        val shopId = settings.getCurrentShop()?.id.toString()

        return shopsApi.getShops(
            appName = APP_NAME,
            appCode = appCode,
            appVersionName = appVersion,
            dateInstall = dateInstall,
            imei = imei,
            dateInit = dateInit,
            shopId = shopId
        ).toDomain()
    }

    private fun List<ShopRemote>.toDomain(): List<Shop> {
        return map { shopRemote ->
            Shop(
                sapId = shopRemote.sapId,
                name = shopRemote.name,
                id = shopRemote.shopId,
                kpp = shopRemote.kpp.orEmpty(),
                inn = shopRemote.inn.orEmpty(),
                hostName = shopRemote.hostname,
                mapPosition = MapPosition(
                    latitude = shopRemote.latitude?.toDouble() ?: DEFAULT_SHOP_LATITUDE,
                    longitude = shopRemote.longitude?.toDouble() ?: DEFAULT_SHOP_LONGITUDE
                )
            )
        }
    }
}
