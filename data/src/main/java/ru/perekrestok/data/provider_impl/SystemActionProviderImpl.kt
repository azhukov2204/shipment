package ru.perekrestok.data.provider_impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import ru.perekrestok.android.extension.getDateInstall
import ru.perekrestok.android.extension.getImei
import ru.perekrestok.android.extension.getVersionCode
import ru.perekrestok.android.extension.getVersionName
import ru.perekrestok.domain.provider.SystemActionProvider
import kotlin.system.exitProcess

class SystemActionProviderImpl(
    private val context: Context
) : SystemActionProvider {

    override fun getAppName(): String {
        return context.getVersionName()
    }

    override fun getAppCode(): Long {
        return context.getVersionCode()
    }

    override fun getDateInstallApp(): String {
        return context.getDateInstall()
    }

    override fun getImei(): String {
        return context.getImei()
    }

    override suspend fun clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            CookieSyncManager.createInstance(context)?.also { syncManager ->
                syncManager.startSync()
                val cookieManager = CookieManager.getInstance()
                cookieManager.removeAllCookie()
                cookieManager.removeSessionCookie()
                syncManager.stopSync()
                syncManager.sync()
            }
        }
    }

    override fun doVibro(time: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            // backward compatibility for Android API < 31,
            // VibratorManager was only added on API level 31 release.
            // noinspection deprecation
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // backward compatibility for Android API < 26
            // noinspection deprecation
            vibrator?.vibrate(time)
        }
    }

    override fun restartApp() {
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val intent = PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1, intent)
        exitProcess(2)
    }
}

