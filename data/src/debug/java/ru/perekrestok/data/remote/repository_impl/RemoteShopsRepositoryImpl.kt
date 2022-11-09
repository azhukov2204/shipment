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
import ru.perekrestok.kotlin.StringPatterns
import ru.perekrestok.kotlin.addToList

/**
 * Это Debug-вариант
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

        private val debugShops = buildList {

            Shop(
                sapId = "SAP1",
                name = "wms.dev92",
                id = 1,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev92.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP2",
                name = "wms.dev29",
                id = 2,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev29.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP3",
                name = "wms.dev62",
                id = 3,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev62.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP4",
                name = "yurov-wms",
                id = 4,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://v.yurov-wms.caladan01-sel.vprok.tech",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP5",
                name = "WMS-DEV-31",
                id = 5,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev31.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP6",
                name = "WMS-DEV-32",
                id = 6,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev32.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
            Shop(
                sapId = "SAP7",
                name = "WMS-DEV-34",
                id = 7,
                kpp = StringPatterns.EMPTY_SYMBOL,
                inn = StringPatterns.EMPTY_SYMBOL,
                hostName = "http://wms.dev34.services.lab.x5.ru",
                mapPosition = MapPosition(0.0, 0.0)
            ).addToList(this)
        }
    }

    override suspend fun getShops(): List<Shop> {
        val appCode = systemActionInteractor.getAppCode().toString()
        val appVersion = systemActionInteractor.getAppName()
        val dateInstall = systemActionInteractor.getDateInstallApp()
        val imei = systemActionInteractor.getImei()
        val settings = settingsInteractor.getAppSettings()
        val dateInit = settings.getDateInitApp().orEmpty()
        val shopId = settings.getCurrentShop()?.id.toString()

        return buildList {
            debugShops.addToList(this)
            shopsApi.getShops(
                appName = APP_NAME,
                appCode = appCode,
                appVersionName = appVersion,
                dateInstall = dateInstall,
                imei = imei,
                dateInit = dateInit,
                shopId = shopId
            ).toDomain().addToList(this)
        }
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
