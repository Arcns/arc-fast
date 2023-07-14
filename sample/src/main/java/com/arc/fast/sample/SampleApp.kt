package com.arc.fast.sample

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import com.arc.fast.immersive.setAutoInitSystemBarHeight

class SampleApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setAutoInitSystemBarHeight()
//        DynamicColors.applyToActivitiesIfAvailable(this) { _, _ ->
//            LocalData.enableDynamicColors
//        }
    }

    companion object {
        lateinit var instance: SampleApp

        @JvmStatic
        val isDebug: Boolean
            get() = BuildConfig.DEBUG

        @JvmStatic
        val packageInfo: PackageInfo
            get() = instance.packageManager.getPackageInfo(instance.packageName, 0)
    }
}