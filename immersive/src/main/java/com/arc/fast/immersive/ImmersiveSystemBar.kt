package com.arc.fast.immersive

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 设置自动初始化状态栏和导航栏高度
 */
fun Application.setAutoInitSystemBarHeight() {
    registerActivityLifecycleCallbacks(
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                if (systemStatusBarHeight > 0) return
                activity.getSystemBarHeight { _, _ -> }
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
        })
}

/**
 * 设置状态栏和导航栏
 */
fun Activity.setSystemBarConfig(
    statusBarConfig: SystemBarConfig?,
    navigationBarConfig: SystemBarConfig?,
    isFirstSet: Boolean = false
) {
    if (statusBarConfig == null && navigationBarConfig == null) return
    // 设置是否显示
    setSystemBarVisible(statusBarConfig?.isVisible, navigationBarConfig?.isVisible)
    if (statusBarConfig?.isVisible == false && navigationBarConfig?.isVisible == false) return
    // 设置是否沉浸式或背景颜色
    setImmersiveSystemBar(
        isImmersiveStatusBar = statusBarConfig?.isImmersive,
        statusBarColor = statusBarConfig?.color,
        isImmersiveNavigationBar = navigationBarConfig?.isImmersive,
        navigationBarColor = navigationBarConfig?.color,
        isNeedPost = !isFirstSet
    )
    // 设置前台颜色
    setSystemBarsForegroundColor(
        isLightStatusBarForegroundColor = statusBarConfig?.isLightForegroundColor,
        isLightNavigationBarForegroundColor = navigationBarConfig?.isLightForegroundColor
    )
}

/**
 * 系统栏配置类
 */
data class SystemBarConfig(
    val isVisible: Boolean? = null,
    val isImmersive: Boolean? = null,
    @ColorInt val color: Int? = null,
    val isLightForegroundColor: Boolean? = color?.let { ColorUtils.calculateLuminance(color) < 0.5 }
) {
    companion object {
        // 白色的系统栏
        @JvmStatic
        val Light: SystemBarConfig
            get() = SystemBarConfig(
                isVisible = true,
                isImmersive = false,
                color = Color.WHITE,
                isLightForegroundColor = false
            )

        // 黑色的系统栏
        @JvmStatic
        val Dark: SystemBarConfig
            get() = SystemBarConfig(
                isVisible = true,
                isImmersive = false,
                color = Color.BLACK,
                isLightForegroundColor = true
            )

        // 沉浸式的系统栏，同时设置为白色的前台颜色
        @JvmStatic
        val ImmersiveAndLightForeground: SystemBarConfig
            get() = SystemBarConfig(
                isVisible = true,
                isImmersive = true,
                isLightForegroundColor = true
            )

        // 沉浸式的系统栏，同时设置为黑色的前台颜色
        @JvmStatic
        val ImmersiveAndDarkForeground: SystemBarConfig
            get() = SystemBarConfig(
                isVisible = true,
                isImmersive = true,
                isLightForegroundColor = false
            )

        // 隐藏系统栏
        @JvmStatic
        val Hide: SystemBarConfig
            get() = SystemBarConfig(
                isVisible = false
            )
    }
}

/**
 * 设置系统栏的前景色
 */
fun Activity.setSystemBarsForegroundColor(
    isLightStatusBarForegroundColor: Boolean? = null,
    isLightNavigationBarForegroundColor: Boolean? = null
) {
    if (isLightStatusBarForegroundColor == null && isLightNavigationBarForegroundColor == null) return
    window.decorView.post {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (isLightStatusBarForegroundColor != null) {
            controller.isAppearanceLightStatusBars = !isLightStatusBarForegroundColor
        }
        if (isLightNavigationBarForegroundColor != null) {
            controller.isAppearanceLightNavigationBars = !isLightNavigationBarForegroundColor
        }
    }
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
//    if (isLight) {
//        window?.decorView?.systemUiVisibility = window?.decorView?.systemUiVisibility?.let {
//            it or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        } ?: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//    } else {
//        window?.decorView?.systemUiVisibility?.run {
//            window?.decorView?.systemUiVisibility =
//                this and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // 与非运算，同java的&~
//        }
//    }
}

/**
 * 当前系统状态栏是否为亮前景色
 */
val Activity.isLightStatusBarForegroundColor: Boolean
    get() = !WindowInsetsControllerCompat(
        window,
        window.decorView
    ).isAppearanceLightStatusBars

/**
 * 当前系统导航栏是否为亮前景色
 */
val Activity.isLightNavigationBarForegroundColor: Boolean
    get() = !WindowInsetsControllerCompat(
        window,
        window.decorView
    ).isAppearanceLightNavigationBars


/**
 * 设置沉浸式系统栏
 */
fun Activity.setImmersiveSystemBar(
    isImmersiveStatusBar: Boolean? = null,
    statusBarColor: Int? = null,
    isImmersiveNavigationBar: Boolean? = null,
    navigationBarColor: Int? = null,
    isNeedPost: Boolean = false
) {
    val immersiveHandler = {
        window.decorView.setPadding(0, 0, 0, 0)
        window.decorView.systemUiVisibility =
            if (isImmersiveStatusBar == true) {
                window.statusBarColor = Color.TRANSPARENT
                if (isImmersiveNavigationBar == true) {
                    window.navigationBarColor = Color.TRANSPARENT
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                } else {
                    if (navigationBarColor != null) {
                        window.navigationBarColor = navigationBarColor
                    }
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            } else {
                if (statusBarColor != null) {
                    window.statusBarColor = statusBarColor
                }
                if (navigationBarColor != null) {
                    window.navigationBarColor = navigationBarColor
                }
                View.SYSTEM_UI_FLAG_VISIBLE
            }
    }
    // 新的设置方法暂时无法灵活控制沉浸式效果，先注释
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//        window.attributes = window.attributes.apply {
//            if (isImmersive) {
//                WindowCompat.setDecorFitsSystemWindows(window, false)
//                window.statusBarColor = Color.TRANSPARENT
//                layoutInDisplayCutoutMode =
//                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//            } else {
//                WindowCompat.setDecorFitsSystemWindows(window, true)
//                statusBarColor?.run {
//                    window.statusBarColor = this
//                }
//                layoutInDisplayCutoutMode =
//                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
//            }
//        }
//        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
////                ViewCompat.setOnApplyWindowInsetsListener(window.decorView, null)
////                if (!isImmersive) {
//            val insets =
//                windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
//            window.decorView.setPadding(
//                insets.left,
//                if (isImmersive) 0 else insets.top,
//                insets.right,
//                insets.bottom
//            )
////                }
//            WindowInsetsCompat.CONSUMED
//        }
//    }
    if (isNeedPost)
        window.decorView.post {
            immersiveHandler()
        }
    else immersiveHandler()
}

/**
 * 设置状态栏和导航栏显示隐藏
 */
fun Activity.setSystemBarVisible(
    isVisibleStatusBar: Boolean? = null,
    isVisibleNavigationBar: Boolean? = null
) {
    if (isVisibleStatusBar == null && isVisibleNavigationBar == null) return
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).apply {
        if (isVisibleStatusBar != null) {
            if (isVisibleStatusBar) show(WindowInsetsCompat.Type.statusBars())
            else hide(WindowInsetsCompat.Type.statusBars())
        }
        if (isVisibleNavigationBar != null) {
            if (isVisibleNavigationBar) show(WindowInsetsCompat.Type.navigationBars())
            else {
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

/**
 * 获取系统栏高度
 */
fun Activity.getSystemBarHeight(
    forceReRequest: Boolean = false, /* 强制重新获取 */
    handler: OnSystemBarHeightCallback
) {
    if (!forceReRequest && realSystemStatusBarHeight != null && realSystemNavigationBarHeight != null) {
        handler.invoke(systemStatusBarHeight, systemNavigationBarHeight)
        requestSystemBarHeightCallback.forEach {
            it.invoke(systemStatusBarHeight, systemNavigationBarHeight)
        }
        requestSystemBarHeightCallback.clear()
    } else {
        requestSystemBarHeight(handler)
    }
}


/**
 * 获取系统栏高度
 */
fun Activity.requestSystemBarHeight(
    handler: OnSystemBarHeightCallback
) {
    requestSystemBarHeightCallback.add(handler)
    (this as? LifecycleOwner)?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
            requestSystemBarHeightCallback.remove(handler)
        }
    })
    window.decorView.requestApplyInsetsWhenAttached {
        Log.e("requestSystemBarHeight", "setOnApplyWindowInsetsListener")
        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView
        ) { _, insets ->
            val statusBarHeight =
                insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top
            // 只有当statusBarHeight>0，才证明是正常的回调
            Log.e("requestSystemBarHeight", "getInsetsIgnoringVisibility:$statusBarHeight")
            if (statusBarHeight > 0) {
                if (realSystemStatusBarHeight == null)
                    realSystemStatusBarHeight = statusBarHeight
                if (realSystemNavigationBarHeight == null)
                    realSystemNavigationBarHeight =
                        insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom
                ViewCompat.setOnApplyWindowInsetsListener(window.decorView, null)
                Log.e("getSystemBarHeight", "setOnApplyWindowInsetsListener null")
                handler.invoke(systemStatusBarHeight, systemNavigationBarHeight)
                requestSystemBarHeightCallback.forEach {
                    it.invoke(systemStatusBarHeight, systemNavigationBarHeight)
                }
                requestSystemBarHeightCallback.clear()
            }
            insets
        }
    }
}

/**
 * 获取系统状态栏高度
 */
var realSystemStatusBarHeight: Int? = null
    private set
val systemStatusBarHeight: Int get() = realSystemStatusBarHeight ?: 0
fun Activity.getStatusBarHeight(handler: (Int) -> Unit) {
    if (realSystemStatusBarHeight != null) handler.invoke(systemStatusBarHeight)
    else getSystemBarHeight { _, _ -> handler.invoke(systemStatusBarHeight) }
//        mSystemStatusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android").let {
//            if (it > 0) resources.getDimensionPixelSize(it)
//            else 0
//        }
}


/**
 * 获取底部导航栏高度
 */
private var realSystemNavigationBarHeight: Int? = null
val systemNavigationBarHeight: Int get() = realSystemNavigationBarHeight ?: 0
fun Activity.getNavigationBarHeight(handler: (Int) -> Unit) {
    if (realSystemNavigationBarHeight != null) handler.invoke(systemNavigationBarHeight)
    else getSystemBarHeight { _, _ -> handler.invoke(systemNavigationBarHeight) }
}

typealias OnSystemBarHeightCallback = (statusBarHeight: Int, navigationBarHeight: Int) -> Unit

private val requestSystemBarHeightCallback by lazy { ArrayList<OnSystemBarHeightCallback>() }


/**
 * 设置padding为系统状态栏高度
 */
fun View.setPaddingForStatusBarHeight(
    notRepeat: Boolean = true, // 不重复添加，若现有padding高度为状态栏高度，则不再添加
    keepOriginalHeight: Boolean = true // 保留原有的高度，在它的基础上叠加
) {
    (context as? Activity)?.getStatusBarHeight { statusBarHeight ->
        if (statusBarHeight == 0) return@getStatusBarHeight
        if (paddingTop != 0 && paddingTop != statusBarHeight && defaultPaddingTop == null) {
            defaultPaddingTop = paddingTop
        }
        val newPaddingTop = statusBarHeight + (defaultPaddingTop ?: 0)
        if (notRepeat && paddingTop == newPaddingTop) {
            return@getStatusBarHeight
        }
        if (keepOriginalHeight && layoutParams.height > 0) {
            layoutParams = layoutParams.apply {
                height += statusBarHeight
            }
        }
        setPadding(
            paddingLeft,
            newPaddingTop,
            paddingRight,
            paddingBottom
        )
    }
}

/**
 * 上外边距增加系统状态栏的高度大小
 */
fun View.setMarginStatusBarHeight(
    notRepeat: Boolean = true, // 不重复添加，若现有padding高度为状态栏高度，则不再添加
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        (context as? Activity)?.getStatusBarHeight { statusBarHeight ->
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (statusBarHeight == 0) return@getStatusBarHeight
                if (topMargin != 0 && topMargin != statusBarHeight && defaultMarginTop == null) {
                    defaultMarginTop = topMargin
                }
                val newMarginTop = statusBarHeight + (defaultMarginTop ?: 0)
                if (notRepeat && topMargin == newMarginTop) {
                    return@getStatusBarHeight
                }
                setMargins(
                    leftMargin,
                    newMarginTop,
                    rightMargin,
                    bottomMargin
                )
            }
        }
    }
}


var View.defaultPaddingTop: Int?
    get() = getTag(R.id.arc_fast_default_padding_top) as? Int
    set(value) {
        setTag(R.id.arc_fast_default_padding_top, value)
    }


var View.defaultMarginTop: Int?
    get() = getTag(R.id.arc_fast_default_margin_top) as? Int
    set(value) {
        setTag(R.id.arc_fast_default_margin_top, value)
    }