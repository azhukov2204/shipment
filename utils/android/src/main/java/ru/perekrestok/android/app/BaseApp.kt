package ru.perekrestok.android.app

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.perekrestok.android.BuildConfig
import timber.log.Timber

abstract class BaseApp : Application() {

    protected open val fragmentLifecycleCallbacks = FragmentLifecycleCallbacks()

    @Suppress("TooManyFunctions")
    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
            this@BaseApp.onActivityPaused(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            this@BaseApp.onActivityResumed(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            this@BaseApp.onActivityStarted(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityStopped(activity: Activity) {
            this@BaseApp.onActivityStopped(activity)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            this@BaseApp.onActivityCreated(activity, savedInstanceState)

            (activity as? AppCompatActivity)
                ?.supportFragmentManager
                ?.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        }

        override fun onActivityDestroyed(activity: Activity) {
            this@BaseApp.onActivityDestroyed(activity)

            (activity as? AppCompatActivity)
                ?.supportFragmentManager
                ?.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        }

        // данный метод добавлен только начиная с 29 версии api, на более ранних версия работать не будет
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
            super.onActivityPostCreated(activity, savedInstanceState)

            this@BaseApp.onActivityPostCreated(activity, savedInstanceState)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostStarted(activity: Activity) {
            super.onActivityPostStarted(activity)

            this@BaseApp.onActivityPostStarted(activity)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostResumed(activity: Activity) {
            super.onActivityPostResumed(activity)

            this@BaseApp.onActivityPostResumed(activity)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostPaused(activity: Activity) {
            super.onActivityPostPaused(activity)

            this@BaseApp.onActivityPostPaused(activity)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostStopped(activity: Activity) {
            super.onActivityPostStopped(activity)

            this@BaseApp.onActivityPostStopped(activity)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onActivityPostDestroyed(activity: Activity) {
            super.onActivityPostDestroyed(activity)

            this@BaseApp.onActivityPostDestroyed(activity)
        }
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@BaseApp)
            modules(predefineKoinModules())
        }
    }

    protected open fun predefineKoinModules(): List<Module> = emptyList()

    protected open inner class FragmentLifecycleCallbacks :
        FragmentManager.FragmentLifecycleCallbacks() {

        @CallSuper
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
        }
    }

    protected open fun onActivityPaused(activity: Activity) {}

    protected open fun onActivityResumed(activity: Activity) {}

    protected open fun onActivityStarted(activity: Activity) {}

    protected open fun onActivityDestroyed(activity: Activity) {}

    protected open fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    protected open fun onActivityStopped(activity: Activity) {}

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostDestroyed(activity: Activity) {
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostStopped(activity: Activity) {
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostPaused(activity: Activity) {
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostResumed(activity: Activity) {
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @RequiresApi(Build.VERSION_CODES.Q)
    protected open fun onActivityPostStarted(activity: Activity) {
    }
}
