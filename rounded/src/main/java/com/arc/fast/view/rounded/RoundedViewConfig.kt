package com.arc.fast.view.rounded

import android.graphics.Color

open class RoundedViewConfig(
    var radius: RoundedRadius = RoundedRadius(),
    var backgroundColor: Int? = null,
    var borderColor: Int? = null,
    var borderSize: Float? = null
) {
    fun getRadii(offset: Float = 0f): FloatArray {
        return floatArrayOf(
            radius.roundedRadiusTopLeft + offset,
            radius.roundedRadiusTopLeft + offset,
            radius.roundedRadiusTopRight + offset,
            radius.roundedRadiusTopRight + offset,
            radius.roundedRadiusBottomRight + offset,
            radius.roundedRadiusBottomRight + offset,
            radius.roundedRadiusBottomLeft + offset,
            radius.roundedRadiusBottomLeft + offset
        )
    }

    val hasRadius: Boolean
        get() = radius.roundedRadiusTopLeft != 0f || radius.roundedRadiusTopRight != 0f || radius.roundedRadiusBottomLeft != 0f || radius.roundedRadiusBottomRight != 0f

    val hasBackgroundColor: Boolean
        get() = backgroundColor != null && backgroundColor != Color.TRANSPARENT


    val hasBorder: Boolean
        get() = borderColor != null && borderColor != Color.TRANSPARENT && borderSize != null && borderSize!! > 0f
}