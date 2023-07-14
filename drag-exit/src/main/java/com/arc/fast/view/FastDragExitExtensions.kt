package com.arc.fast.view

import android.view.View
import android.view.ViewGroup


internal inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParams<ViewGroup.LayoutParams>(block)
}

internal inline val View.marginLeft: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0

internal inline val View.marginTop: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0

@JvmName("updateLayoutParamsTyped")
internal inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(
    block: T.() -> Unit
) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}