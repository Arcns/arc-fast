package com.arc.fast.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arc.fast.core.R


/**
 * 沉浸式弹窗
 */
abstract class ImmersiveDialog : DialogFragment() {
    // 沉浸式弹窗工具
    protected val immersiveDialogConfigUtil by lazy {
        ImmersiveDialogConfigUtil(immersiveDialogConfig)
    }

    // 沉浸式配置
    protected abstract val immersiveDialogConfig: ImmersiveDialogConfig?

    // 布局id
    protected abstract val layoutId: Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersiveDialogConfigUtil.applyConfigToDialog(this, null)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return immersiveDialogConfigUtil.wrapDialogContentView(this, inflater, layoutId)
    }
}

/**
 * 沉浸式弹窗工具
 */
class ImmersiveDialogConfigUtil(val defaultConfig: ImmersiveDialogConfig? = null) {

    /**
     * 应用配置到弹窗，请在Dialog的onViewCreated中调用
     */
    fun applyConfigToDialog(
        dialogFragment: DialogFragment?,
        config: ImmersiveDialogConfig? = null
    ) {
        val window = dialogFragment?.dialog?.window ?: return
        val dialogConfig = config ?: defaultConfig ?: return
        dialogFragment.dialog?.apply {
            setCancelable(dialogConfig.cancelable)
            setCanceledOnTouchOutside(dialogConfig.canceledOnTouchOutside)
        }
        window.attributes = window.attributes.apply {
            gravity = dialogConfig.gravity
            width = WindowManager.LayoutParams.MATCH_PARENT
            if (!dialogConfig.enableSoftInputAdjustResize) {
                height = WindowManager.LayoutParams.MATCH_PARENT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                flags =
                    (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).let {
                        flags and it.inv() or (it and it)
                    }
            } else {
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            if (dialogConfig.backgroundDimAmount != -1f) {
                dimAmount = dialogConfig.backgroundDimAmount
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                fitInsetsTypes = fitInsetsTypes and WindowInsetsCompat.Type.systemBars().inv()
            }
        }
        window.apply {
            // 删除默认背景
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置动画
            setWindowAnimations(dialogConfig.animations)
            //禁用默认的dialog外屏幕变暗效果，禁用后dimAmount将无效
            if (!dialogConfig.backgroundDimEnabled) {
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            if (!dialogConfig.enableSoftInputAdjustResize) {
                // 控制状态栏和导航栏
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
                WindowCompat.setDecorFitsSystemWindows(this, false)
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    )
                //内容扩展到导航栏，该设置会导致无法修改前景颜色
                // setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
                // 状态栏和导航栏前景颜色
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars =
                        !dialogConfig.isLightStatusBarForegroundColor
                    isAppearanceLightNavigationBars =
                        !dialogConfig.isLightNavigationBarForegroundColor
                }
                // 打开键盘时不重置弹窗布局大小
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            } else {
                // 控制状态栏和导航栏
                WindowCompat.setDecorFitsSystemWindows(this, true)
                // 打开键盘时自动重置弹窗布局大小，避免布局被键盘遮挡
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                // 关闭时收起软键盘
                dialogFragment.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    android.R.style.Theme_Black_NoTitleBar_Fullscreen
                )
            }
        }
        dialogConfig.updateCustomDialogConfig?.invoke(dialogFragment.dialog!!, window)
    }

    /**
     * 包装沉浸式父控件，请在Dialog的onCreateView中调用，并在onCreateView中return包装后的content view
     */
    fun wrapDialogContentView(
        dialogFragment: DialogFragment,
        inflater: LayoutInflater,
        contentView: Int,
        config: ImmersiveDialogConfig? = null
    ): View = wrapDialogContentView(
        dialogFragment,
        inflater,
        inflater.inflate(contentView, null, false),
        config
    )

    /**
     * 包装沉浸式父控件，请在Dialog的onCreateView中调用，并在onCreateView中return包装后的content view
     */
    fun wrapDialogContentView(
        dialogFragment: DialogFragment,
        inflater: LayoutInflater,
        contentView: View,
        config: ImmersiveDialogConfig? = null
    ): View {
        val dialogConfig = config ?: defaultConfig
        if (dialogConfig != null && !dialogConfig.backgroundDimEnabled && (dialogConfig.backgroundColor != Color.TRANSPARENT || dialogConfig.navigationColor != Color.TRANSPARENT)) {
            ImmersiveDialogBackground(dialogFragment, inflater, dialogConfig).show()
        }
        return if (
            dialogConfig == null ||
            !dialogConfig.enableWrapDialogContentView ||
            (dialogConfig.width == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.height == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.navigationColor == Color.TRANSPARENT)
        ) {
            contentView
        } else {
            val rootView = createImmersiveDialogRootView(inflater, dialogConfig)
            val viewNavigationBarBg =
                rootView.findViewById<View>(R.id.arc_fast_view_navigation_bar_bg)
            if (dialogConfig.canceledOnTouchOutside) {
                rootView.setOnClickListener { dialogFragment.dismiss() }
                contentView.setOnClickListener { }
            }
            // 添加到父控件
            rootView.addView(
                contentView,
                0,
                ConstraintLayout.LayoutParams(dialogConfig.width, dialogConfig.height).apply {
                    if (dialogConfig.gravity == Gravity.CENTER || dialogConfig.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                        rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = viewNavigationBarBg.id
                        if (dialogConfig.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                            height = 0
                        }
                    } else {
                        leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                        rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = viewNavigationBarBg.id
                    }
                })
            rootView
        }
    }
}

private fun createImmersiveDialogRootView(
    inflater: LayoutInflater, dialogConfig: ImmersiveDialogConfig
): ViewGroup {
    val rootView =
        inflater.inflate(R.layout.arc_fast_view_immersive_dialog_root, null) as ViewGroup
    val viewNavigationBarBg =
        rootView.findViewById<View>(R.id.arc_fast_view_navigation_bar_bg)
    viewNavigationBarBg.apply {
        if (dialogConfig.navigationColor != Color.TRANSPARENT && systemNavigationBarHeight > 0) {
            layoutParams.height = systemNavigationBarHeight
            setBackgroundColor(dialogConfig.navigationColor)
            isVisible = true
        } else isVisible = false
    }
    return rootView
}

/**
 * 沉浸式弹窗背景
 * 防止当有弹窗动画时，不能直接设置背景色，否则背景会跟随弹窗动画，需要把背景add到activity的window中
 */
class ImmersiveDialogBackground(
    val dialogFragment: DialogFragment,
    inflater: LayoutInflater,
    val dialogConfig: ImmersiveDialogConfig
) {
    private val rootView: View
    private val backgroundView: View
    private val navigationBarView: View?
    private val animator: ObjectAnimator
    private val windowManager: WindowManager by lazy {
        dialogFragment.activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val currentIsAppearanceLightNavigationBars =
        !dialogConfig.isLightNavigationBarForegroundColor
    private val currentIsLightStatusBarForegroundColor =
        !dialogConfig.isLightStatusBarForegroundColor

    //
    private val parentWindow by lazy {
        findParentWindow()
    }
    private val parentWindowController by lazy {
        if (dialogConfig.enableWrapDialogContentView) null
        else parentWindow?.let { WindowInsetsControllerCompat(it, it.decorView) }
    }
    private var parentWindowIsAppearanceLightNavigationBars: Boolean? = null
    private var parentWindowIsAppearanceLightStatusBars: Boolean? = null

    init {
        if (dialogConfig.enableWrapDialogContentView || dialogConfig.navigationColor == Color.TRANSPARENT) {
            rootView = View(inflater.context)
            backgroundView = rootView
            navigationBarView = null
        } else {
            rootView = createImmersiveDialogRootView(inflater, dialogConfig)
            backgroundView = View(inflater.context)
            navigationBarView = rootView.findViewById<View>(R.id.arc_fast_view_navigation_bar_bg)
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
            setBackgroundColor(dialogConfig.backgroundColor)
            if (dialogConfig.canceledOnTouchOutside) {
                setOnClickListener {
                    dialogFragment.dismiss()
                }
            }
        }
        animator = ObjectAnimator.ofFloat(backgroundView, "alpha", 0f, 1f)
            .setDuration(300).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        if (backgroundView.alpha == 0f) {
                            windowManager.removeView(
                                rootView
                            )
                        }
                    }
                })
            }

    }

    private fun findParentWindow(): Window? {
        val firstDialogFragment = (dialogFragment.parentFragmentManager.fragments.firstOrNull {
            it is DialogFragment && it.isVisible && !it.isRemoving && it.dialog?.isShowing == true
        } ?: dialogFragment.activity?.supportFragmentManager?.fragments?.firstOrNull {
            it is DialogFragment && it.isVisible && !it.isRemoving && it.dialog?.isShowing == true
        }) as? DialogFragment
        return firstDialogFragment?.dialog?.window
            ?: dialogFragment.activity?.window
    }

    private fun executeShowAnimator() {
        animator.cancel()
        backgroundView.alpha = 0f
        backgroundView.isVisible = true
        animator.setFloatValues(0f, 1f)
        animator.startDelay = 100
        animator.start()
        // 修改状态栏/导航栏前景色
        if (parentWindowController != null) {
            parentWindowController?.isAppearanceLightNavigationBars =
                currentIsAppearanceLightNavigationBars
            parentWindowController?.isAppearanceLightStatusBars =
                currentIsLightStatusBarForegroundColor
        }
    }

    private fun executeDismissAnimator() {
        navigationBarView?.isVisible = false
        animator.cancel()
        animator.setFloatValues(1f, 0f)
        animator.startDelay = 0
        animator.start()
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
    }

    fun show() {
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
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
            ).apply {
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
        // 显示时尝试设置背景
        dialogFragment.dialog?.setOnShowListener {
            executeShowAnimator()
        }
        // 弹窗关闭时，删除背景
        dialogFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                dialogFragment.lifecycle.removeObserver(this)
                super.onDestroy(owner)
                executeDismissAnimator()
            }
        })
    }
}

/**
 * 沉浸式弹窗配置
 */
class ImmersiveDialogConfig(
    var width: Int,
    var height: Int,
    var gravity: Int,
    // 必须禁用backgroundDimEnabled时，backgroundColor才会生效；注意如果您想要实现多个弹窗覆盖时的背景效果，您需要准备多个弹窗使用的是同一个FragmentManager
    var backgroundColor: Int,
    var navigationColor: Int,
    var backgroundDimEnabled: Boolean,
    // 0完全透明，1不透明
    var backgroundDimAmount: Float,
    var animations: Int,
    var canceledOnTouchOutside: Boolean,
    var cancelable: Boolean,
    var isLightStatusBarForegroundColor: Boolean,
    var isLightNavigationBarForegroundColor: Boolean,
    // 启用根视图包裹
    var enableWrapDialogContentView: Boolean = true,
    // 启用打开键盘时自动重置弹窗布局大小，避免布局被键盘遮挡。
    // 注意启用后，内容无法扩展到全屏，沉浸式背景颜色仅支持backgroundDimAmount
    var enableSoftInputAdjustResize: Boolean = false,
    // 更新dialog更多自定义配置
    var updateCustomDialogConfig: ((dialog: Dialog, window: Window) -> Unit)? = null
) {
    companion object {
        @JvmStatic
        fun createFullScreenDialogConfig(): ImmersiveDialogConfig {
            return ImmersiveDialogConfig(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                false,
                0f,
                R.style.StyleArcFastAnimDialogScaleEnterExit,
                true,
                true,
                true,
                true
            )
        }

        @JvmStatic
        fun createBottomDialogConfig(): ImmersiveDialogConfig {
            return ImmersiveDialogConfig(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
                Color.TRANSPARENT,
                Color.WHITE,
                true,
                -1f,
                R.style.StyleArcFastAnimDialogDownEnterExit,
                true,
                true,
                true,
                false
            )
        }

        @JvmStatic
        fun createSoftInputAdjustResizeDialogConfig(): ImmersiveDialogConfig {
            return ImmersiveDialogConfig(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.BOTTOM,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                true,
                -1f,
                R.style.StyleArcFastAnimDialogDownEnterExit,
                true,
                true,
                true,
                true,
                enableSoftInputAdjustResize = true,
                enableWrapDialogContentView = false
            )
        }
    }
}