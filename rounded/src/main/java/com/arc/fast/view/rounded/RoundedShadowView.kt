package com.arc.fast.view.rounded

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.arc.fast.rounded.R
import kotlin.math.max
import kotlin.math.roundToInt

open class RoundedShadowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundedView(context, attrs, defStyleAttr) {

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        if (!isInEditMode) {
            initShadow(context, attrs)
        }
    }

    companion object {
        // 用于计算阴影Padding的系数值
        const val shadowMultiplier = 1.5f
    }

    // 阴影的模糊度
    var shadowBlur = 0f
        private set

    // 阴影X方向的偏移值
    var shadowOffsetX = 0f
        private set

    // 阴影Y方向的偏移值
    var shadowOffsetY = 0f
        private set

    // 阴影的颜色
    var shadowColor = Color.TRANSPARENT
        private set

    // 阴影的Padding(实际占用空间大小)
    var shadowPadding = 0f
        private set

    override val defaultDrawOffset: Float get() = shadowPadding

    private fun initShadow(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return
        val typedArray =
            attrs?.let { context.obtainStyledAttributes(it, R.styleable.RoundedShadowView, 0, 0) }
                ?: return
        try {
            shadowBlur =
                typedArray.getDimension(
                    R.styleable.RoundedShadowView_rounded_shadow_blur,
                    0f
                )
            shadowOffsetX =
                typedArray.getDimension(
                    R.styleable.RoundedShadowView_rounded_shadow_offsetX,
                    0f
                )
            shadowOffsetY =
                typedArray.getDimension(
                    R.styleable.RoundedShadowView_rounded_shadow_offsetY,
                    0f
                )
            shadowColor =
                typedArray.getColor(
                    R.styleable.RoundedShadowView_rounded_shadow_color,
                    Color.TRANSPARENT
                )
        } finally {
            typedArray.recycle()
        }
        refreshShadow(invalidate = true)
    }

    fun refreshShadow(
        blur: Float = shadowBlur,
        offsetX: Float = shadowOffsetX,
        offsetY: Float = shadowOffsetY,
        color: Int = shadowColor,
        invalidate: Boolean = true
    ) {
        shadowBlur = blur
        shadowOffsetX = offsetX
        shadowOffsetY = offsetY
        shadowColor = color
        shadowPadding = blur * shadowMultiplier + max(offsetX, offsetY)
        _config.paint.setShadowLayer(blur, offsetX, offsetY, color)
        if (invalidate) invalidate()
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        // 预留阴影空间
        super.layout(
            (l - shadowPadding).roundToInt(),
            (t - shadowPadding).roundToInt(),
            (r + shadowPadding).roundToInt(),
            (b + shadowPadding).roundToInt()
        )
    }


}