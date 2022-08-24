package com.arc.fast.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.arc.fast.core.R

/**
 * 沉浸式PopupWindow
 */
abstract class ImmersivePopupWindow : PopupWindow {
    private var mY = 0
    private var mContext: Context? = null
    private var mWindowManager: WindowManager? = null
    private var mWindow: Window? = null
    private var mBackgroundView: View? = null
    private var mImmersivePopupWindowConfig: ImmersivePopupWindowConfig? = null
    private val hasInit: Boolean get() = mContext != null && mWindowManager != null && mWindow != null && mBackgroundView != null && mImmersivePopupWindowConfig != null
    private val mBackgroundViewAnimator by lazy {
        ObjectAnimator.ofFloat(mBackgroundView, "alpha", 0f, 1f)
            .setDuration(300).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        if (mBackgroundView?.alpha == 0f) mBackgroundView?.isVisible = false
                    }
                })
            }
    }

    // 原本的状态栏导航栏配置
    private var mOriginalIsLightStatusBarForegroundColor: Boolean? = null
    private var mOriginalIsLightNavigationBarForegroundColor: Boolean? = null

    // 是否正在显示
    private var isShow = false


    constructor(context: Context) : super(context)
    constructor(width: Int, height: Int) : super(width, height)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initImmersivePopupWindow(context)
    }

    constructor(contentView: View?, width: Int, height: Int, focusable: Boolean)
            : super(contentView, width, height, focusable) {
        initImmersivePopupWindow(contentView?.context)
    }

    override fun setContentView(contentView: View?) {
        super.setContentView(contentView)
        initImmersivePopupWindow(contentView?.context)
    }

    /**
     * 沉浸式PopupWindow配置
     */
    abstract fun getImmersivePopupWindowConfig(context: Context): ImmersivePopupWindowConfig


    /**
     * 初始化沉浸式PopupWindow
     */
    private fun initImmersivePopupWindow(context: Context?) {
        if (context == null || mContext != null) return
        mContext = context
        mWindowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        mWindow = (mContext as? Activity)?.window
        mImmersivePopupWindowConfig = getImmersivePopupWindowConfig(context)
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        initBackgroundView(anchor)
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        onShow(anchor, yoff)
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        initBackgroundView(parent)
        super.showAtLocation(parent, gravity, x, y)
        onShow(parent)
    }

    override fun dismiss() {
        onDismiss()
        super.dismiss()
    }

    /**
     * 初始化背景
     */
    private fun initBackgroundView(anchor: View) {
        if (mWindow == null) {
            mWindow = (anchor.context as? Activity)?.window
        }
        if (mBackgroundView != null || mImmersivePopupWindowConfig == null) return
        val config = mImmersivePopupWindowConfig ?: return
        mBackgroundView = View(mContext).apply {
            isVisible = false
            setBackgroundColor(config.backgroundColor)
            fitsSystemWindows = false
            if (config.canceledOnTouchOutside) {
                setOnClickListener {
                    dismiss()
                }
            }
            if (config.cancelable) {
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
            }
        }
        mWindowManager?.addView(
            mBackgroundView,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
            ).apply {
                token = mWindow!!.decorView.windowToken
                gravity = Gravity.CENTER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    fitInsetsTypes =
                        if (config.enableNavigationBar) WindowInsets.Type.navigationBars() else WindowInsets.Type.ime()
                }
            }
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            mBackgroundView?.systemUiVisibility =
                if (config.enableNavigationBar) View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                else View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    /**
     * 显示回调
     */
    private fun onShow(anchor: View, yoff: Int = 0) {
        isShow = true
        if (!hasInit) return
        val config = mImmersivePopupWindowConfig ?: return
        if (config.enableBackgroundAnimator) {
            mBackgroundViewAnimator.cancel()
            mBackgroundView?.alpha = 0f
            mBackgroundView?.isVisible = true
            mBackgroundViewAnimator.setFloatValues(0f, 1f)
            mBackgroundViewAnimator.startDelay = 100
            mBackgroundViewAnimator.start()
        } else {
            mBackgroundView?.isVisible = true
        }
        // 设置状态栏和导航栏的前景颜色
        if (config.isLightStatusBarForegroundColor != null || config.isLightNavigationBarForegroundColor != null) {
            val controller =
                WindowInsetsControllerCompat(mWindow!!, mBackgroundView!!)
            val currentIsLightStatusBarForegroundColor = !controller.isAppearanceLightStatusBars
            val currentIsLightNavigationBarForegroundColor =
                !controller.isAppearanceLightNavigationBars
            if (config.isLightStatusBarForegroundColor != null && config.isLightStatusBarForegroundColor != currentIsLightStatusBarForegroundColor) {
                mOriginalIsLightStatusBarForegroundColor = currentIsLightStatusBarForegroundColor
                controller.isAppearanceLightStatusBars = !config.isLightStatusBarForegroundColor!!
            }
            if (config.isLightNavigationBarForegroundColor != null && config.isLightNavigationBarForegroundColor != currentIsLightNavigationBarForegroundColor) {
                mOriginalIsLightNavigationBarForegroundColor =
                    currentIsLightNavigationBarForegroundColor
                controller.isAppearanceLightNavigationBars =
                    !config.isLightNavigationBarForegroundColor!!
            }
        }
        // 是否启用锚点
        if (config.enableAnchorView) {
            val location = IntArray(2)
            anchor.getLocationInWindow(location)
            val anchorY = location.last()
            if (mY != anchorY) {
                mY = anchorY + anchor.measuredHeight + yoff
                mWindowManager?.updateViewLayout(
                    mBackgroundView,
                    (mBackgroundView?.layoutParams as? WindowManager.LayoutParams)?.apply {
                        this.y = mY
                        this.height = mWindow!!.decorView.measuredHeight - mY
                    })
            }
        }
    }

    /**
     * 关闭回调
     */
    private fun onDismiss() {
        if (!isShow) return
        isShow = false
        if (!hasInit) return
        val config = mImmersivePopupWindowConfig ?: return
        if (config.enableBackgroundAnimator) {
            mBackgroundViewAnimator.cancel()
            mBackgroundViewAnimator.setFloatValues(1f, 0f)
            mBackgroundViewAnimator.startDelay = 0
            mBackgroundViewAnimator.start()
        } else {
            mBackgroundView?.isVisible = false
        }
        // 恢复为显示之前的颜色
        if (mOriginalIsLightStatusBarForegroundColor != null || mOriginalIsLightNavigationBarForegroundColor != null) {
            val controller =
                WindowInsetsControllerCompat(mWindow!!, mBackgroundView!!)
            if (mOriginalIsLightStatusBarForegroundColor != null)
                controller.isAppearanceLightStatusBars = !mOriginalIsLightStatusBarForegroundColor!!
            if (mOriginalIsLightNavigationBarForegroundColor != null)
                controller.isAppearanceLightNavigationBars =
                    !mOriginalIsLightNavigationBarForegroundColor!!
        }
    }
}

class ImmersivePopupWindowConfig(
    /**
     * 背景颜色
     */
    var backgroundColor: Int = Color.TRANSPARENT,
    /**
     * 是否点击背景时关闭
     */
    var canceledOnTouchOutside: Boolean = true,
    /**
     * 点击返回键时关闭
     */
    var cancelable: Boolean = true,
    /**
     * 状态栏前景颜色
     */
    var isLightStatusBarForegroundColor: Boolean? = null,
    /**
     * 导航栏前景颜色
     */
    var isLightNavigationBarForegroundColor: Boolean? = null,
    /**
     * 背景是否启用锚视图（背景显示在锚视图之下）
     */
    var enableAnchorView: Boolean = false,
    /**
     * 是否显示导航栏（启用时背景将不包含导航栏）
     */
    var enableNavigationBar: Boolean = false,
    /**
     * 是否启用背景渐变动画
     */
    var enableBackgroundAnimator: Boolean = true
) {
    companion object {

        /**
         * 底部PopupWindows
         */
        @JvmStatic
        fun createBottomPopupWindow(context: Context) = ImmersivePopupWindowConfig(
            backgroundColor = ContextCompat.getColor(
                context,
                R.color.arc_fast_popup_window_background
            ),
            isLightStatusBarForegroundColor = true,
            isLightNavigationBarForegroundColor = null,
            enableAnchorView = false,
            enableNavigationBar = true
        )

        /**
         * 顶部PopupWindows
         */
        @JvmStatic
        fun createTopPopupWindow(context: Context) = ImmersivePopupWindowConfig(
            backgroundColor = ContextCompat.getColor(
                context,
                R.color.arc_fast_popup_window_background
            ),
            isLightStatusBarForegroundColor = null,
            isLightNavigationBarForegroundColor = true,
            enableAnchorView = true,
            enableNavigationBar = false
        )

    }
}