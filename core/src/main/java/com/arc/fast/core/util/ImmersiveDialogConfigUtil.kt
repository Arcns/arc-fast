package com.arc.fast.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
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
            height = WindowManager.LayoutParams.MATCH_PARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            flags = (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS).let {
                flags and it.inv() or (it and it)
            }
            if (dialogConfig.backgroundDimAmount != -1f) {
                dimAmount = dialogConfig.backgroundDimAmount
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
            // 控制状态栏和导航栏
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(this, false)
//            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            //内容扩展到导航栏，改设置会导致无法修改前景颜色
            // setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

        }
        // 状态栏和导航栏前景颜色
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars =
                !dialogConfig.isLightStatusBarForegroundColor
            isAppearanceLightNavigationBars =
                !dialogConfig.isLightNavigationBarForegroundColor
        }
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
        return if (dialogConfig == null || (dialogConfig.width == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.height == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.backgroundColor == Color.TRANSPARENT && dialogConfig.navigationColor == Color.TRANSPARENT)) {
            contentView
        } else {
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
            if (dialogConfig.canceledOnTouchOutside) {
                rootView.setOnClickListener { dialogFragment.dismiss() }
                contentView.setOnClickListener { }
            }
            // 设置背景颜色
            if (!dialogConfig.backgroundDimEnabled && dialogConfig.backgroundColor != Color.TRANSPARENT) {
                if (dialogConfig.animations == 0) {
                    // 没有弹窗动画时，直接设置背景色即可
                    rootView.setBackgroundColor(dialogConfig.backgroundColor)
                } else {
                    val windowManager =
                        dialogFragment.activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    // 有弹窗动画时，不能直接设置背景色，否则背景会跟随弹窗动画，需要把背景add到activity的window中
                    val dialogBackgroundView = View(inflater.context).apply {
                        isVisible = false
                        setBackgroundColor(dialogConfig.backgroundColor)
                        fitsSystemWindows = false
//                        systemUiVisibility =
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        if (dialogConfig.canceledOnTouchOutside) {
                            setOnClickListener {
                                dialogFragment.dismiss()
                            }
                        }
//                        if (dialogConfig.cancelable) {
//                            setOnKeyListener { _, keyCode, _ ->
//                                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                                    dialogFragment.dismiss()
//                                    return@setOnKeyListener true
//                                }
//                                false
//                            }
//                        }
                    }
                    val backgroundViewAnimator =
                        ObjectAnimator.ofFloat(dialogBackgroundView, "alpha", 0f, 1f)
                            .setDuration(300).apply {
                                addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        if (dialogBackgroundView.alpha == 0f) {
                                            windowManager.removeView(
                                                dialogBackgroundView
                                            )
                                        }
                                    }
                                })
                            }
                    windowManager.addView(
                        dialogBackgroundView,
                        WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                            PixelFormat.TRANSLUCENT
                        ).apply {
                            token = dialogFragment.activity?.window?.decorView?.windowToken
                            gravity = Gravity.CENTER
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                layoutInDisplayCutoutMode =
                                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                            }
                            flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        }
                    )
                    // 显示时尝试设置背景
                    dialogFragment.dialog?.setOnShowListener {
                        backgroundViewAnimator.cancel()
                        dialogBackgroundView.alpha = 0f
                        dialogBackgroundView.isVisible = true
                        backgroundViewAnimator.setFloatValues(0f, 1f)
                        backgroundViewAnimator.startDelay = 100
                        backgroundViewAnimator.start()
                    }
                    // 弹窗关闭时，删除背景
                    dialogFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            dialogFragment.lifecycle.removeObserver(this)

                            backgroundViewAnimator.cancel()
                            backgroundViewAnimator.setFloatValues(1f, 0f)
                            backgroundViewAnimator.startDelay = 0
                            backgroundViewAnimator.start()

                            super.onDestroy(owner)
                        }
                    })
                }
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

/**
 * 获取当前弹窗所在FragmentManager中最顶层的弹窗
 */
fun DialogFragment.getCurrentDialogInFragmentManager(): DialogFragment? =
    try {
        parentFragmentManager.fragments.lastOrNull {
            it is DialogFragment && it.dialog?.isShowing == true && it != this
        } as? DialogFragment
    } catch (e: Exception) {
        null
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
    var isLightNavigationBarForegroundColor: Boolean
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
    }
}