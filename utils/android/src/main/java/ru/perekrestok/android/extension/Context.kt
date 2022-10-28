package ru.perekrestok.android.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat.getLongVersionCode
import ru.perekrestok.android.BuildConfig
import ru.perekrestok.kotlin.StringPatterns
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val NUMBER_FOR_CONVERT = 1000
private const val DATE_FORMAT = "dd.MM kk:mm"
private const val SUB_PATH_TO_DOWNLOADS = "/Download/tso/"
private const val APK_MIME_TYPE = "application/vnd.android.package-archive"
private const val APK_FILENAME_EXTENSION = ".apk"
private const val DEFAULT_VERSION_CODE = 0L

fun Activity.hideKeyboard() {
    inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

fun Context.getVersionName(): String =
    packageManager?.let { packageManager ->
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    } ?: StringPatterns.EMPTY_SYMBOL

fun Context.getVersionCode(): Long {
    return packageManager?.let { packageManager ->
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        getLongVersionCode(packageInfo)
    } ?: DEFAULT_VERSION_CODE
}

fun Context.isIntentResolved(intent: Intent): Boolean {
    val info = packageManager.queryIntentActivities(intent, 0)
    return info.size > 0
}

@Suppress("DEPRECATION")
@SuppressLint("MissingPermission", "HardwareIds")
fun Context.getImei(): String {
    return try {
        return when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.O -> telephonyManager.imei
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> telephonyManager.deviceId
            else -> Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    } catch (ex: Exception) {
        Timber.e(ex)
        StringPatterns.EMPTY_SYMBOL
    }
}

fun Context.getDateInstall(): String {
    return try {
        val date = Date(getDateInstallMills())
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        dateFormat.format(date)
    } catch (ex: Exception) {
        Timber.e(ex)
        StringPatterns.EMPTY_SYMBOL
    }
}

@Throws(Exception::class)
fun Context.getDateInstallMills(): Long {
    return applicationContext
        .packageManager
        .getPackageInfo(applicationContext.packageName, 0)
        .firstInstallTime
}

val Context.telephonyManager get() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

val Context.inputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

val Context.layoutInflater get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.clipboardManager get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

val Context.notificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.bluetoothManager get() = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

@AttrRes
fun Context.attr(@AttrRes attrResColor: Int): Int {
    return this.resolveThemeColor(attrResColor)
}

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableRes)
}

fun Context.drawableWithColor(@DrawableRes drawableRes: Int, @AttrRes attrResColor: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableRes)?.apply {
        setTint(resolveThemeColor(attrResColor))
    }
}

fun Context.dpToPx(dp: Int): Int = dpToPx(dp.toFloat())

fun Context.dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).toInt()

fun Context.spToPx(sp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

@AttrRes
fun Context.resolveThemeColor(@AttrRes attrResColor: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrResColor, typedValue, true)
    return typedValue.data
}

fun Context.dimen(@DimenRes dimenRes: Int) = resources.getDimensionPixelSize(dimenRes)

val Context.displayWidthPx get() = resources.displayMetrics.widthPixels

val Context.displayHeightPx get() = resources.displayMetrics.heightPixels

data class AlertDialogButton(
    val text: String = StringPatterns.EMPTY_SYMBOL,
    val onClicked: () -> Unit = {}
)

fun Context.isFlashSupported(): Boolean {
    return packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) == true
}

fun Context.getLocationManager(): LocationManager {
    return getSystemService(Context.LOCATION_SERVICE) as LocationManager
}

enum class FileDirectoryPath(val directoryPath: String, val isCachedDir: Boolean) {
    WEB_VIEW_CACHE("/web_view_cache", false),
    OTHER("", true)
}

fun Context.getDirectoryPatch(fileDirectoryPath: FileDirectoryPath): String {
    val storageDirectory =
        if (fileDirectoryPath.isCachedDir) cacheDir
        else filesDir

    return storageDirectory.absolutePath + fileDirectoryPath.directoryPath
}



