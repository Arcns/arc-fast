package com.arc.fast.core

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat


/**
 * 获取当前APP的版本名
 */
val Context.versionName: String
    get() = packageManager.getPackageInfo(
        packageName, 0
    ).versionName

/**
 * 获取当前APP的版本号
 */
val Context.versionCode: Long
    get() = PackageInfoCompat.getLongVersionCode(
        packageManager.getPackageInfo(
            packageName,
            0
        )
    )

/**
 * 获取屏幕大小（会自动减去窗口装饰）
 */
val Context.screenSize: Size
    get() = resources.displayMetrics.let {
        Size(
            it.widthPixels,
            it.heightPixels
        )
    }

/**
 * 获取屏幕宽度（会自动减去窗口装饰）
 */
val Context.screenWidth: Int get() = screenSize.width

/**
 * 获取屏幕高度（会自动减去窗口装饰）
 */
val Context.screenHeight: Int get() = screenSize.height

/**
 * 获取真实的屏幕大小（不会减去窗口装饰）
 */
val Context.realScreenSize: Size
    get() {
        val windowManager = getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.let {
                Size(it.width(), it.height())
            }
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }


/**
 * 获取真实的屏幕宽度（不会减去窗口装饰）
 */
val Context.realScreenWidth: Int get() = realScreenSize.width

/**
 * 获取真实的屏幕高度（不会减去窗口装饰）
 */
val Context.realScreenHeight: Int get() = realScreenSize.height

