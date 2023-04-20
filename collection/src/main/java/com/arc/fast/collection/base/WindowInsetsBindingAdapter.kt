package com.arc.fast.collection.base

import android.view.View
import androidx.databinding.BindingAdapter
import com.arc.fast.immersive.applySystemWindowsInsetsMargin
import com.arc.fast.immersive.applySystemWindowsInsetsPadding


@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun applySystemWindowsInsetsPadding(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    view.applySystemWindowsInsetsPadding(applyLeft, applyTop, applyRight, applyBottom)
}


@BindingAdapter(
    "marginLeftSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginRightSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun applySystemWindowsInsetsMargin(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    view.applySystemWindowsInsetsMargin(applyLeft, applyTop, applyRight, applyBottom)
}