package ru.perekrestok.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header

internal interface ShopsApi {
    @GET("tsom/config/shops")
    suspend fun getShops(
        @Header("App") appName: String,
        @Header("App-Version") appCode: String,
        @Header("App-Version-Name") appVersionName: String,
        @Header("X-Device") imei: String,
        @Header("Date-Init") dateInit: String,
        @Header("Date-Install") dateInstall: String,
        @Header("Shop-Id") shopId: String,
    ): List<ShopRemote>
}

internal data class ShopRemote(

    @SerializedName("shop_hostname_id")
    val shopHostnameId: Int,

    @SerializedName("shop_id")
    val shopId: Int,

    @SerializedName("sap_id")
    val sapId: String,

    @SerializedName("hostname")
    val hostname: String,

    @SerializedName("ip")
    val ip: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("region_id")
    val regionId: Int,

    @SerializedName("latitude")
    val latitude: String?,

    @SerializedName("longitude")
    val longitude: String?,

    @SerializedName("inn")
    val inn: String?,

    @SerializedName("kpp")
    val kpp: String?
)
