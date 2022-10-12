package com.arc.fast.immersive

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible


/**
 * 沉浸式PopupWindow
 */
abstract class ImmersivePopupWindow : PopupWindow {
    private var mContext: Context? = null
    private var mImmersivePopupWindowConfig: ImmersivePopupWindowConfig? = null
    private var mBackground: ImmersivePopupWindowBackground? = null
    private val hasInit: Boolean get() = mContext != null && mImmersivePopupWindowConfig != null && mBackground != null

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
        mImmersivePopupWindowConfig = getImmersivePopupWindowConfig(context)
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        initBackground(mContext ?: anchor.context)
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        onShow(anchor, yoff)
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        initBackground(mContext ?: parent.context)
        super.showAtLocation(parent, gravity, x, y)
        onShow(parent)
    }

    private fun initBackground(context: Context) {
        if (mBackground?.isDestroy == false) return
        mBackground = mImmersivePopupWindowConfig?.let {
            ImmersivePopupWindowBackground(
                context,
                this,
                it
            ).init()
        }
    }

    override fun dismiss() {
        onDismiss()
        super.dismiss()
    }

    /**
     * 显示回调
     */
    private fun onShow(anchor: View, yoff: Int = 0) {
        isShow = true
        if (!hasInit) return
        mBackground?.show(anchor, yoff)
    }

    /**
     * 关闭回调
     */
    private fun onDismiss() {
        if (!isShow) return
        isShow = false
        if (!hasInit) return
        mBackground?.dismiss()
    }
}

/**
 * 沉浸式PopupWindow背景
 */
class ImmersivePopupWindowBackground(
    val context: Context,
    val popupWIndow: PopupWindow,
    val config: ImmersivePopupWindowConfig
) {
    private val rootView: View
    private val backgroundView: View
    private val navigationBarView: View?
    private val animator: ObjectAnimator
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val currentIsAppearanceLightNavigationBars =
        config.isLightNavigationBarForegroundColor?.let { !it }
    private val currentIsLightStatusBarForegroundColor =
        config.isLightStatusBarForegroundColor?.let { !it }
    private var currentY = -1
    var isDestroy = false
        private set

    //
    private val parentWindow by lazy {
        findParentWindow()
    }
    private val parentWindowController by lazy {
        parentWindow?.let { WindowInsetsControllerCompat(it, it.decorView) }
    }
    private var parentWindowIsAppearanceLightNavigationBars: Boolean? = null
    private var parentWindowIsAppearanceLightStatusBars: Boolean? = null

    init {
        if (config.navigationColor == Color.TRANSPARENT) {
            rootView = View(context)
            backgroundView = rootView
            navigationBarView = null
        } else {
            rootView = LayoutInflater.from(context)
                .inflate(R.layout.arc_fast_view_immersive_dialog_root, null) as ViewGroup
            navigationBarView =
                rootView.findViewById<View>(R.id.arc_fast_view_navigation_bar_bg).apply {
                    if (config.navigationColor != Color.TRANSPARENT && systemNavigationBarHeight > 0) {
                        layoutParams.height = systemNavigationBarHeight
                        setBackgroundColor(config.navigationColor)
                        isVisible = true
                    } else isVisible = false
                }
            backgroundView = View(context)
            rootView.addView(
                backgroundView,
                0,
                ConstraintLayout.LayoutParams(0, 0).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                })
        }
        rootView.apply {
            fitsSystemWindows = false
//            systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        backgroundView.apply {
            isVisible = false
            setBackgroundColor(config.backgroundColor)
            if (config.canceledOnTouchOutside) {
                setOnClickListener {
                    popupWIndow.dismiss()
                }
            }
        }
        if (config.cancelable) {
//            popupWIndow.setBackgroundDrawable(BitmapDrawable())
//            popupWIndow.isFocusable = true
            rootView.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWIndow.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
        }

        animator = ObjectAnimator.ofFloat(backgroundView, "alpha", 0f, 1f)
            .setDuration(300).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (backgroundView.alpha == 0f) {
                            dismissBackground()
                        }
                    }
                })
            }

    }

    private fun findParentWindow(): Window? {
        return (context as? Activity)?.window
    }

    fun init(): ImmersivePopupWindowBackground {
        if (parentWindowController != null) {
            if (parentWindowController?.isAppearanceLightNavigationBars != currentIsAppearanceLightNavigationBars) {
                parentWindowIsAppearanceLightNavigationBars =
                    parentWindowController?.isAppearanceLightNavigationBars
            }
            if (parentWindowController?.isAppearanceLightStatusBars != currentIsLightStatusBarForegroundColor) {
                parentWindowIsAppearanceLightStatusBars =
                    parentWindowController?.isAppearanceLightStatusBars
            }
        }
        windowManager.addView(
            rootView,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL /*背景外可透传*/
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            ).apply {
                flags = flags and (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE).inv()
                token = parentWindow?.decorView?.windowToken
                gravity = Gravity.CENTER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    fitInsetsTypes = fitInsetsTypes and WindowInsetsCompat.Type.systemBars().inv()
                }
            }
        )
        return this
    }

    fun show(anchor: View? = null, yoff: Int = 0) {
        // 修改状态栏/导航栏前景色
        if (parentWindowController != null) {
            if (currentIsAppearanceLightNavigationBars != null)
                parentWindowController?.isAppearanceLightNavigationBars =
                    currentIsAppearanceLightNavigationBars
            if (currentIsLightStatusBarForegroundColor != null)
                parentWindowController?.isAppearanceLightStatusBars =
                    currentIsLightStatusBarForegroundColor
        }
        // 是否启用锚点
        if (anchor != null && config.backgroundConstraint != ImmersivePopupWindowBackgroundConstraint.UnConstraint) {
            anchor.post {
                val location = IntArray(2)
                anchor.getLocationInWindow(location)
                val anchorY: Int
                val height: Int
                val gravity: Int
                when (config.backgroundConstraint) {
                    ImmersivePopupWindowBackgroundConstraint.TopToAnchorBottom -> {
                        anchorY = location.last() + anchor.measuredHeight + yoff
                        height = parentWindow!!.decorView.measuredHeight - anchorY
                        gravity = Gravity.TOP
                    }
                    ImmersivePopupWindowBackgroundConstraint.BottomToAnchorTop -> {
                        anchorY = 0
                        height = location.last() - yoff
                        gravity = Gravity.TOP
                    }
                    else -> {
                        showBackground()
                        return@post
                    }
                }
                if (currentY != anchorY) {
                    currentY = anchorY
                    windowManager.updateViewLayout(
                        rootView,
                        (rootView.layoutParams as? WindowManager.LayoutParams)?.apply {
                            this.y = currentY
                            this.gravity = gravity
                            this.height = height
                        })
                }
                showBackground()
            }
        } else {
            showBackground()
        }
    }

    private fun showBackground() {
        if (config.enableBackgroundAnimator) {
            animator.cancel()
            backgroundView.alpha = 0f
            backgroundView.isVisible = true
            animator.setFloatValues(0f, 1f)
            animator.startDelay = 100
            animator.start()
        } else {
            backgroundView.isVisible = true
        }
    }

    private fun dismissBackground() {
        if ((context as? Activity)?.let { it.isFinishing || it.isDestroyed } == true) return
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismiss() {
        if (config.enableBackgroundAnimator) {
            navigationBarView?.isVisible = false
            animator.cancel()
            animator.setFloatValues(1f, 0f)
            animator.startDelay = 0
            animator.start()
        } else {
            dismissBackground()
        }
        // 恢复状态栏/导航栏前景色
        if (parentWindowController != null) {
            if (parentWindowIsAppearanceLightNavigationBars != null) {
                parentWindowController?.isAppearanceLightNavigationBars =
                    parentWindowIsAppearanceLightNavigationBars!!
            }
            if (parentWindowIsAppearanceLightStatusBars != null) {
                parentWindowController?.isAppearanceLightStatusBars =
                    parentWindowIsAppearanceLightStatusBars!!
            }
        }
        isDestroy = true
    }
}

class ImmersivePopupWindowConfig(
    /**
     * 背景颜色
     */
    var backgroundColor: Int = Color.TRANSPARENT,
    /**
     * 导航栏颜色
     */
    var navigationColor: Int = Color.TRANSPARENT,
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
     * 背景布局约束
     */
    var backgroundConstraint: ImmersivePopupWindowBackgroundConstraint = ImmersivePopupWindowBackgroundConstraint.UnConstraint,
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
            isLightNavigationBarForegroundColor = false,
            navigationColor = Color.WHITE
        )

        /**
         * 顶部锚点PopupWindows
         */
        @JvmStatic
        fun createTopToAnchorBottomPopupWindow(context: Context) = ImmersivePopupWindowConfig(
            backgroundColor = ContextCompat.getColor(
                context,
                R.color.arc_fast_popup_window_background
            ),
            isLightStatusBarForegroundColor = null,
            isLightNavigationBarForegroundColor = null,
            backgroundConstraint = ImmersivePopupWindowBackgroundConstraint.TopToAnchorBottom,
        )

        /**
         * 底部锚点PopupWindows
         */
        @JvmStatic
        fun createBottomToAnchorTopPopupWindow(context: Context) = ImmersivePopupWindowConfig(
            backgroundColor = ContextCompat.getColor(
                context,
                R.color.arc_fast_popup_window_background
            ),
            isLightStatusBarForegroundColor = null,
            isLightNavigationBarForegroundColor = null,
            backgroundConstraint = ImmersivePopupWindowBackgroundConstraint.BottomToAnchorTop,
        )

    }
}

/**
 * 沉浸式PopupWindow背景布局约束
 */
enum class ImmersivePopupWindowBackgroundConstraint {
    /**
     * 无约束，即全屏
     */
    UnConstraint,

    /**
     * 显示在Anchor下方
     */
    TopToAnchorBottom,

    /**
     * 显示在Anchor上方
     */
    BottomToAnchorTop
}