package com.arc.fast.immersive

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.*

/**
 * 监听应用Insets
 */
fun View.doOnApplyWindowInsets(f: (view: View, insets: WindowInsetsCompat, margins: MarginPaddings, paddings: MarginPaddings) -> Unit) {
    // 获取默认状态的大小
    val initialMargins = getMargins()
    val initialPaddings = getPaddings()
    // 注册应用Insets事件，并传入默认大小
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialMargins, initialPaddings)
        // Always return the insets, so that children can also use them
        insets
    }
    // 请求应用Insets
    requestApplyInsetsWhenAttached()
}

data class MarginPaddings(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int
)

/**
 * 返回paddings
 */
fun View.getPaddings() = MarginPaddings(
    paddingLeft, paddingTop, paddingRight, paddingBottom
)

/**
 * 返回margins
 */
fun View.getMargins() = MarginPaddings(
    marginLeft, marginTop, marginRight, marginBottom
)

/**
 * 请求应用Insets
 */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // 已添加到window时，重新请求应用Insets
        requestApplyInsets()
    } else {
        // 未添加到window时，添加监听器在添加到window时请求应用Insets
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

//@BindingAdapter(
//    "paddingLeftSystemWindowInsets",
//    "paddingTopSystemWindowInsets",
//    "paddingRightSystemWindowInsets",
//    "paddingBottomSystemWindowInsets",
//    requireAll = false
//)
fun View.applySystemWindowsInsetsPadding(
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    doOnApplyWindowInsets { view, insets, _, paddings ->
        val systemBarsInsets =
            insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
        val left = if (applyLeft) systemBarsInsets.left else 0
        val top = if (applyTop) systemBarsInsets.top else 0
        val right = if (applyRight) systemBarsInsets.right else 0
        val bottom = if (applyBottom) systemBarsInsets.bottom else 0
        view.setPadding(
            paddings.left + left,
            paddings.top + top,
            paddings.right + right,
            paddings.bottom + bottom
        )
    }
}


//@BindingAdapter(
//    "marginLeftSystemWindowInsets",
//    "marginTopSystemWindowInsets",
//    "marginRightSystemWindowInsets",
//    "marginBottomSystemWindowInsets",
//    requireAll = false
//)
fun View.applySystemWindowsInsetsMargin(
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    doOnApplyWindowInsets { view, insets, margins, _ ->
        val systemBarsInsets =
            insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
        val left = if (applyLeft) systemBarsInsets.left else 0
        val top = if (applyTop) systemBarsInsets.top else 0
        val right = if (applyRight) systemBarsInsets.right else 0
        val bottom = if (applyBottom) systemBarsInsets.bottom else 0
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margins.left + left
            topMargin = margins.top + top
            rightMargin = margins.right + right
            bottomMargin = margins.bottom + bottom
        }
    }
}

/**
 * 实现视图跟随键盘变化播放插帧动画
 * 在 API 30+ 的设备上运行时，此功能在 IME 进入/退出屏幕时完美跟踪它
 * 在 API 21-29 的设备上运行时，WindowInsetsAnimationCompat将运行一个动画，该动画试图模仿系统 IME 动画.
 * 在 API < 21 的设备时,只能直接显示/隐藏
 * https://github.com/android/user-interface-samples/tree/main/WindowInsetsAnimation
 */
fun View.applyWindowInsetIMEAnimation(
    persistentInsetTypes: Int = WindowInsetsCompat.Type.systemBars(),
    dispatchMode: Int = WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP,
    rootView: View? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && rootView != null) {
        // 低于R版本的系统目前在dialog下会有无法接收到ime Insets回调的bug,因此暂时不做监听
        val deferringInsetsListener = RootViewDeferringInsetsCallback(
            persistentInsetTypes = WindowInsetsCompat.Type.systemBars(),
            deferredInsetTypes = WindowInsetsCompat.Type.ime()
        )
        ViewCompat.setWindowInsetsAnimationCallback(rootView, deferringInsetsListener)
        ViewCompat.setOnApplyWindowInsetsListener(rootView, deferringInsetsListener)
    }
    ViewCompat.setWindowInsetsAnimationCallback(
        this,
        TranslateDeferringInsetsAnimationCallback(
            view = this,
            persistentInsetTypes = persistentInsetTypes,
            deferredInsetTypes = WindowInsetsCompat.Type.ime(),
            dispatchMode = dispatchMode
        )
    )
}

/**
 * 实现视图跟随inset变化播放插帧动画
 * 在 API 30+ 的设备上运行时，此功能在 IME 进入/退出屏幕时完美跟踪它
 * 在 API 21-29 的设备上运行时，WindowInsetsAnimationCompat将运行一个动画，该动画试图模仿系统 IME 动画.
 * 在 API < 21 的设备时,只能直接显示/隐藏
 * https://github.com/android/user-interface-samples/tree/main/WindowInsetsAnimation
 */
class TranslateDeferringInsetsAnimationCallback(
    private val view: View,
    private val persistentInsetTypes: Int,
    private val deferredInsetTypes: Int,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimationCompat.Callback(dispatchMode) {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsetsCompat.Type values"
        }
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        val typesInset = insets.getInsets(deferredInsetTypes)
        val otherInset = insets.getInsets(persistentInsetTypes)
        val diff = Insets.subtract(typesInset, otherInset).let {
            Insets.max(it, Insets.NONE)
        }

        view.translationX = (diff.left - diff.right).toFloat()
        view.translationY = (diff.top - diff.bottom).toFloat()

        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        view.translationX = 0f
        view.translationY = 0f
    }
}

/**
 * 根视图延迟插入回调
 * 与TranslateDeferringInsetsAnimationCallback配合使用,使Insets Animation更加流畅,减少跳动和裁剪感
 * https://github.com/android/user-interface-samples/tree/main/WindowInsetsAnimation
 */
class RootViewDeferringInsetsCallback(
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    OnApplyWindowInsetsListener {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsetsCompat.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsetsCompat? = null

    private var deferredInsets = false

    override fun onApplyWindowInsets(
        v: View,
        windowInsets: WindowInsetsCompat
    ): WindowInsetsCompat {
        view = v
        lastWindowInsets = windowInsets

        val types = when {
            deferredInsets -> persistentInsetTypes
            else -> persistentInsetTypes or deferredInsetTypes
        }

        val typeInsets = windowInsets.getInsets(types)
        v.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)

        return WindowInsetsCompat.CONSUMED
    }

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
        if (animation.typeMask and deferredInsetTypes != 0) {
            deferredInsets = true
        }
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnims: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        if (deferredInsets && (animation.typeMask and deferredInsetTypes) != 0) {
            deferredInsets = false
            if (lastWindowInsets != null && view != null) {
                ViewCompat.dispatchApplyWindowInsets(view!!, lastWindowInsets!!)
            }
        }
    }
}
