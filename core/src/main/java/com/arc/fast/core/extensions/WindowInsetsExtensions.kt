package com.arc.fast.core.extensions

import android.view.View
import android.view.ViewGroup
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
