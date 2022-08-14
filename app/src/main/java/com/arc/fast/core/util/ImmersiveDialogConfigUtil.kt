package com.arc.fast.core.util

import android.graphics.Color
import android.os.Build
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arc.fast.core.R

/**
 * 沉浸式视窗工具
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
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            if (dialogConfig.backgroundDimAmount != -1f) {
                dimAmount = dialogConfig.backgroundDimAmount
            }
        }
        window.apply {
            setGravity(dialogConfig.gravity)
//            setWindowAnimations(0)
            setWindowAnimations(dialogConfig.animations)
            //禁用默认的dialog外屏幕变暗效果，禁用后dimAmount将无效
            if (!dialogConfig.backgroundDimEnabled) {
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            // 控制状态栏和导航栏
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
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
        return if (dialogConfig == null || dialogConfig.width == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.height == ViewGroup.LayoutParams.MATCH_PARENT && dialogConfig.backgroundColor == Color.TRANSPARENT && dialogConfig.navigationColor == Color.TRANSPARENT) {
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
                    // 有弹窗动画时，不能直接设置背景色，否则背景会跟随弹窗动画，需要把背景设置到下一层window的view中
                    var dialogBackgroundView: View? = null
                    dialogFragment.dialog?.setOnShowListener {
                        // 显示时尝试设置背景
                        if (dialogBackgroundView != null) return@setOnShowListener
                        ((dialogFragment.getCurrentDialogInFragmentManager()?.dialog?.window?.decorView as? ViewGroup)
                            ?: dialogFragment.activity?.window?.decorView?.findViewById(android.R.id.content))?.apply {
                            dialogBackgroundView = View(inflater.context).apply {
                                setBackgroundColor(dialogConfig.backgroundColor)
                            }
                            addView(
                                dialogBackgroundView,
                                ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            )
                        }
                    }
                    // 弹窗关闭时，删除背景
                    dialogFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            dialogFragment.lifecycle.removeObserver(this)
                            (dialogBackgroundView?.parent as? ViewGroup)?.removeView(
                                dialogBackgroundView
                            )
                            dialogBackgroundView = null
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
                true,
                0f,
                0,
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