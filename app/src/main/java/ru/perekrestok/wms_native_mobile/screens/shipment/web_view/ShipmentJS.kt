package ru.perekrestok.wms_native_mobile.screens.shipment.web_view

@Suppress("TooManyFunctions")
interface ShipmentJS {

    /**
     * Получить версию приложения
     */
    @android.webkit.JavascriptInterface
    fun getAppVersion(): String

    /**
     * Получить код приложения
     */
    @android.webkit.JavascriptInterface
    fun getAppCode(): Long

    /**
     * Получить дату инициализации
     */
    @android.webkit.JavascriptInterface
    fun getDateInitApp(): String

    /**
     * Получить дату установки приложения
     */
    @android.webkit.JavascriptInterface
    fun getDateInstallApp(): String

    /**
     * Получить IMEI
     */
    @android.webkit.JavascriptInterface
    fun getImei(): String

    /**
     * Получить значение из конфига Native
     */
    @android.webkit.JavascriptInterface
    fun getNativeConfigValue(key: String?, type: String?): String?

    /**
     * Получить значение из конфига App
     */
    @android.webkit.JavascriptInterface
    fun getAppConfigValue(key: String?, type: String?): String?

    /**
     * Установить значение в конфиг Native
     */
    @android.webkit.JavascriptInterface
    fun setNativeConfigValue(key: String?, type: String?, value: String?): Boolean

    /**
     * Установить значение в конфиг App
     */
    @android.webkit.JavascriptInterface
    fun setAppConfigValue(key: String?, type: String?, value: String?): Boolean

    /**
     * Подключено ли к принтеру
     */
    @android.webkit.JavascriptInterface
    fun isPrinterConnected(): Boolean

    /**
     * Подключиться к принтеру
     */
    @android.webkit.JavascriptInterface
    fun connectToPrinter()

    /**
     * Отправить данные на принтер
     */
    @android.webkit.JavascriptInterface
    fun sendDataToPrinter(data: String?, encoding: String?)

    /**
     * Отключиться от принтера
     */
    @android.webkit.JavascriptInterface
    fun disconnectFromPrinter()

    /**
     * Отправить данные с подключением и последующим отключением
     */
    @android.webkit.JavascriptInterface
    fun sendDataLonelyToPrinter(data: String?, encoding: String?)

    /**
     * Очистить кэш
     */
    @android.webkit.JavascriptInterface
    fun clearCache()
    /**
     * Очистить кэш
     */
    @android.webkit.JavascriptInterface
    fun clearCookies()

    /**
     * Установить режим кэширования
     */
    @android.webkit.JavascriptInterface
    fun setCacheMode(mode: String)

    /**
     * Установить настройку использования сканера
     */
    @android.webkit.JavascriptInterface
    fun setInternalScannerEnabled(isEnabled: Boolean)

    /**
     * Открыть фотосканнер
     * closeAfterScan - закрывать ли окно после сканирования
     */
    @android.webkit.JavascriptInterface
    fun startScanByPhoto(closeAfterScan: Boolean)

    /**
     * Отобразить диалог
     */
    @android.webkit.JavascriptInterface
    fun showDialog(message: String, buttonText: String)

    /**
     * Отобразить Snackbar
     */
    @android.webkit.JavascriptInterface
    fun showSnackbar(message: String)

    /**
     * Отобразить Toast
     */
    @android.webkit.JavascriptInterface
    fun showToast(message: String)

    /**
     * Отобразить долгий Toast
     */
    @android.webkit.JavascriptInterface
    fun showToastLong(message: String)

    /**
     * Отобразить ошибку в виде Snackbar
     */
    @android.webkit.JavascriptInterface
    fun showError(message: String, details: String)

    /**
     * Вибрировать
     */
    @android.webkit.JavascriptInterface
    fun doVibro(time: Long)

    /**
     * Рестарт WebView
     */
    @android.webkit.JavascriptInterface
    fun restartWebView()

    /**
     * Рестарт приложения
     */
    @android.webkit.JavascriptInterface
    fun restartApp()

    /**
     * Разрешить обновлять страницу по SwipeRefresh
     */
    @android.webkit.JavascriptInterface
    fun setEnabledSwipeToRefresh(isEnabled: Boolean)
}
