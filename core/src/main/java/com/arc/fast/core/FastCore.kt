package com.arc.fast.core

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle

class FastCore {
    private lateinit var mApplicationContext: Context
    private var mInitializationCompleted = false
    val applicationContext: Context
        get() {
            if (!mInitializationCompleted) throw IllegalStateException("Cannot find context from the FastCore,Please complete the initialization first.")
            return mApplicationContext
        }

    private fun init(applicationContext: Context?) {
        if (applicationContext == null || mInitializationCompleted) return
        setApplicationContext(applicationContext)
    }

    fun setApplicationContext(applicationContext: Context) {
        mApplicationContext = applicationContext
        if (!mInitializationCompleted) {
            mInitializationCompleted = true
            (mApplicationContext.applicationContext as Application).registerActivityLifecycleCallbacks(
                object : FastActivityLifecycleCallbacks() {
                    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                    }
                })
        }
    }

    companion object {
        // 单例对象
        @JvmStatic
        val instance: FastCore by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FastCore()
        }

        // 初始化
        @JvmStatic
        fun initialization(context: Context?) {
            instance.init(context)
        }

        @JvmStatic
        val context: Context
            get() = instance.applicationContext
    }
}


class FastCoreInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        FastCore.initialization(context?.applicationContext)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}

abstract class FastActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}