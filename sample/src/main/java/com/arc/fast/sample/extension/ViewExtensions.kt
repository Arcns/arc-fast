package com.arc.fast.sample.extension

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


fun Activity.applyFullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes = window.attributes.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.setLightSystemBar(value: Boolean) {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = value
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = value
}

val Toolbar.titleTextView: TextView?
    get() {
        (0..childCount).forEach {
            val view = getChildAt(it)
            if (view is TextView && (title.isNullOrBlank() || view.text.toString() == title.toString())) {
                return view
            }
        }
        return null
    }