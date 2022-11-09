package ru.perekrestok.domain.entity

@Suppress("TooManyFunctions")
data class AppSetting<T>(val key: SettingKey, val value: T) {
    companion object {
        private const val SCREEN_ORIENTATION_LANDSCAPE = 0
        private const val WEB_VIEW_CACHE_MODE_LOAD_DEFAULT = -1
        private const val SHOP_URL_DEFAULT_SUFFIX = "/pdt2.1/app"
        private const val SCAN_MODE_DEFAULT = 0

        fun List<AppSetting<*>>.getIsAdminMode(): Boolean = findSettingByKey(SettingKey.IS_ADMIN_MODE) ?: false

        fun List<AppSetting<*>>.getCurrentShop(): Shop? = findSettingByKey(SettingKey.SELECTED_SHOP)

        fun List<AppSetting<*>>.getSelectedPrinter(): Device? = findSettingByKey(SettingKey.SELECTED_PRINTER)

        fun List<AppSetting<*>>.getSelectedScanner(): Device? = findSettingByKey(SettingKey.SELECTED_SCANNER)

        fun List<AppSetting<*>>.getScreenOrientation(): Int = findSettingByKey(SettingKey.SCREEN_ORIENTATION)
            ?: SCREEN_ORIENTATION_LANDSCAPE

        fun List<AppSetting<*>>.getIsCookiesAllowed(): Boolean = findSettingByKey(SettingKey.IS_COOKIES_ALLOWED) ?: true

        fun List<AppSetting<*>>.getIsCacheAllowed(): Boolean = findSettingByKey(SettingKey.IS_CACHE_ALLOWED) ?: true

        fun List<AppSetting<*>>.getWebViewCacheMode(): Int = findSettingByKey(SettingKey.WEB_VIEW_CACHE_MODE)
            ?: WEB_VIEW_CACHE_MODE_LOAD_DEFAULT

        fun List<AppSetting<*>>.getHostnameSuffix(): String = findSettingByKey(SettingKey.HOSTNAME_SUFFIX)
            ?: SHOP_URL_DEFAULT_SUFFIX

        fun List<AppSetting<*>>.getDateInitApp(): String? = findSettingByKey(SettingKey.DATE_INIT)

        fun List<AppSetting<*>>.getCustomEndpoint(): String? = findSettingByKey(SettingKey.CUSTOM_ENDPOINT)

        fun List<AppSetting<*>>.getScanMode(): ScanMode {
            val scanModeId = findSettingByKey(SettingKey.SCAN_MODE_ID) ?: SCAN_MODE_DEFAULT
            return ScanMode.getById(scanModeId)
        }

        fun List<AppSetting<*>>.getSettingValueByKey(key: String): String? {
            return SettingKey.getByKeyName(key)?.let { settingKey ->
                this.findSettingByKey<Any>(settingKey).toString()
            }
        }

        private inline fun <reified T> List<AppSetting<*>>.findSettingByKey(settingKey: SettingKey): T? {
            return find { setting -> setting.key == settingKey }
                ?.let { setting -> setting.value as T }
        }
    }
}

enum class SettingKey(val keyName: String, val settingType: SettingType) {
    DATE_INIT("date_init", SettingType.STRING),
    IS_ADMIN_MODE("is_admin_mode", SettingType.BOOLEAN),
    SELECTED_SHOP("selected_shop", SettingType.SHOP),

    @Deprecated("Логика внутреннего сканера уже не используется")
    IS_USE_INTERNAL_SCANNER("use_internal_scanner", SettingType.BOOLEAN),
    SELECTED_PRINTER("selected_printer", SettingType.DEVICE),
    SELECTED_SCANNER("selected_scanner", SettingType.DEVICE),
    SCREEN_ORIENTATION("orientation", SettingType.INT),
    IS_COOKIES_ALLOWED("use_shop_emulator_cookies", SettingType.BOOLEAN),
    IS_CACHE_ALLOWED("use_shop_emulator_cache", SettingType.BOOLEAN),
    WEB_VIEW_CACHE_MODE("shop_emulator_cache_mode", SettingType.INT),
    CUSTOM_ENDPOINT("custom_endpoint", SettingType.STRING),
    HOSTNAME_SUFFIX("shop_emulator_hostname_suffix", SettingType.STRING),
    SCAN_MODE_ID("scan_mode_id", SettingType.INT);

    companion object {
        fun getByKeyName(keyName: String): SettingKey? {
            return values().find { it.keyName.equals(keyName, ignoreCase = true) }
        }
    }
}

enum class SettingType(val typeName: String) {
    STRING("string"),
    INT("int"),
    FLOAT("float"),
    LONG("long"),
    BOOLEAN(""),
    SHOP("shop"),
    DEVICE("device");

    companion object {
        fun getByTypeName(typeName: String): SettingType {
            return values().find { it.typeName == typeName } ?: STRING
        }
    }
}
