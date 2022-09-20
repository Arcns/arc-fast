package com.arc.fast.sample

import android.app.Application
import android.content.pm.PackageInfo
import com.arc.fast.core.FastCore
import com.arc.fast.sample.data.LocalData

class SampleApp : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalData.initialize(this)
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