package ru.perekrestok.domain.entity

import kotlinx.serialization.Serializable


@Serializable
data class Shop(
    val id: Int,
    val name: String,
    val sapId: String,
    val kpp: String,
    val inn: String,
    val hostName: String,
    val mapPosition: MapPosition
) {

    companion object {
        private const val BASIC_URL_PREFIX = "http://"
        private const val BASIC_URL_FORMAT = "http://%s"
    }

    fun getShopUrl(): String {
        return if (!hostName.startsWith(BASIC_URL_PREFIX)) {
            String.format(BASIC_URL_FORMAT, hostName)
        } else hostName
    }
}
