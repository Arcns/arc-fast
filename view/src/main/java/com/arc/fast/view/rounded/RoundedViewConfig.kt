package com.arc.fast.view.rounded

import android.graphics.Color

open class RoundedViewConfig(
    var radius: RoundedRadius = RoundedRadius(),
    var backgroundColor: Int? = null,
    var borderColor: Int? = null,
    var borderSize: Float? = null
) {
    fun getRadii(): FloatArray {
        return floatArrayOf(
            radius.roundedRadiusTopLeft,
            radius.roundedRadiusTopLeft,
            radius.roundedRadiusTopRight,
            radius.roundedRadiusTopRight,
            radius.roundedRadiusBottomRight,
            radius.roundedRadiusBottomRight,
            radius.roundedRadiusBottomLeft,
            radius.roundedRadiusBottomLeft
        )
    }

    val hasRadius: Boolean
        get() = radius.roundedRadiusTopLeft != 0f || radius.roundedRadiusTopRight != 0f || radius.roundedRadiusBottomLeft != 0f || radius.roundedRadiusBottomRight != 0f

    val hasBackgroundColor: Boolean
        get() = backgroundColor != null && backgroundColor != Color.TRANSPARENT


    val hasBorder: Boolean
        get() = borderColor != null && borderColor != Color.TRANSPARENT && borderSize != null && borderSize!! > 0f
}