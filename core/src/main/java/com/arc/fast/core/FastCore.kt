package com.arc.fast.core

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.arc.fast.core.util.tryInvoke
import com.arc.fast.immersive.setAutoInitSystemBarHeight

class FastCore {
    private lateinit var mApplicationContext: Context
    private var mInitializationCompleted = false
    val applicationContext: Context
        get() {
            if (!mInitializationCompleted) throw IllegalStateException("Cannot find context from the FastCore,Please complete the initialization first.")
            return mApplicationContext
        }

    // 沉浸式模块是否可用
    val immersiveIsAvailable: Boolean by lazy {
        tryInvoke {
            Class.forName("com.arc.fast.immersive.SystemBarConfig")
        }
    }

    private fun init(applicationContext: Context?) {
        if (applicationContext == null || mInitializationCompleted) return
        setApplicationContext(applicationContext)
    }

    fun setApplicationContext(applicationContext: Context) {
        mApplicationContext = applicationContext
        if (!mInitializationCompleted) {
            mInitializationCompleted = true
            if (immersiveIsAvailable) {
                (mApplicationContext.applicationContext as Application).setAutoInitSystemBarHeight()
            }
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