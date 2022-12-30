package com.arc.fast.view.rounded

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.arc.fast.view.R

interface IRoundedView {
    var _config: RoundedViewConfig
    var _temporarilyConfig: RoundedViewConfig?

    val roundedRadiusTopLeft: Float get() = _config.radius.roundedRadiusTopLeft
    val roundedRadiusTopRight: Float get() = _config.radius.roundedRadiusTopRight
    val roundedRadiusBottomLeft: Float get() = _config.radius.roundedRadiusBottomLeft
    val roundedRadiusBottomRight: Float get() = _config.radius.roundedRadiusBottomRight
    val roundedView: View? get() = this as? View

    val enableRoundedRadius: Boolean get() = true

    fun setRoundedBackgroundColor(@ColorInt roundedBackgroundColor: Int) {
        _config.backgroundColor = roundedBackgroundColor
        roundedView?.invalidate()
    }

    fun changeRoundedRadiusValue(
        roundedRadiusTopLeft: Float?,
        roundedRadiusTopRight: Float?,
        roundedRadiusBottomLeft: Float?,
        roundedRadiusBottomRight: Float?
    ) {
        if (roundedRadiusTopLeft != null) _config.radius.roundedRadiusTopLeft =
            roundedRadiusTopLeft
        if (roundedRadiusTopRight != null) _config.radius.roundedRadiusTopRight =
            roundedRadiusTopRight
        if (roundedRadiusBottomLeft != null) _config.radius.roundedRadiusBottomLeft =
            roundedRadiusBottomLeft
        if (roundedRadiusBottomRight != null) _config.radius.roundedRadiusBottomRight =
            roundedRadiusBottomRight
    }

    /**
     * 初始化读取参数
     */
    fun initRoundedRadius(context: Context, attrs: AttributeSet) {
        if (roundedView?.isInEditMode == true) return
        roundedView?.setWillNotDraw(false)
        // TypeArray中含有我们需要使用的参数
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedView, 0, 0) ?: return
        try {
            val roundedRadiusTopLeft =
                typedArray.getDimension(R.styleable.RoundedView_rounded_radius_top_left, 0f)
            val roundedRadiusTopRight =
                typedArray.getDimension(R.styleable.RoundedView_rounded_radius_top_right, 0f)
            val roundedRadiusBottomLeft =
                typedArray.getDimension(R.styleable.RoundedView_rounded_radius_bottom_left, 0f)
            val roundedRadiusBottomRight =
                typedArray.getDimension(R.styleable.RoundedView_rounded_radius_bottom_right, 0f)
            changeRoundedRadiusValue(
                roundedRadiusTopLeft,
                roundedRadiusTopRight,
                roundedRadiusBottomLeft,
                roundedRadiusBottomRight
            )
            if (!_config.hasRadius) {
                val roundedRadius =
                    typedArray.getDimension(R.styleable.RoundedView_rounded_radius, 0f)
                changeRoundedRadiusValue(
                    roundedRadius, roundedRadius, roundedRadius, roundedRadius
                )
            }
            _config.backgroundColor =
                typedArray.getColor(
                    R.styleable.RoundedView_rounded_background_color,
                    Color.TRANSPARENT
                )
            _config.borderColor =
                typedArray.getColor(
                    R.styleable.RoundedView_rounded_border_color,
                    Color.TRANSPARENT
                )
            _config.borderSize =
                typedArray.getDimension(R.styleable.RoundedView_rounded_border_size, 0f)
        } finally {
            typedArray.recycle()
        }
    }

    fun setRoundedRadius(roundedRadius: Float) {
        changeRoundedRadiusValue(roundedRadius, roundedRadius, roundedRadius, roundedRadius)
        roundedView?.invalidate()
    }

    fun setRoundedRadius(
        roundedRadiusTopLeft: Float,
        roundedRadiusTopRight: Float,
        roundedRadiusBottomLeft: Float,
        roundedRadiusBottomRight: Float
    ) {
        changeRoundedRadiusValue(
            roundedRadiusTopLeft = roundedRadiusTopLeft,
            roundedRadiusTopRight = roundedRadiusTopRight,
            roundedRadiusBottomLeft = roundedRadiusBottomLeft,
            roundedRadiusBottomRight = roundedRadiusBottomRight
        )
        roundedView?.invalidate()
    }

    fun setRoundedRadius(
        roundedRadius: RoundedRadius
    ) {
        _config.radius = roundedRadius
        roundedView?.invalidate()
    }

    fun setTemporarilyRoundedRadius(
        roundedRadiusTopLeft: Float,
        roundedRadiusTopRight: Float,
        roundedRadiusBottomLeft: Float,
        roundedRadiusBottomRight: Float,
        roundedBackgroundColor: Int? = null,
        roundedBorderColor: Int? = null,
        roundedBorderSize: Float? = null
    ) {
        setTemporarilyRoundedRadius(
            temporarilyRoundedRadius = RoundedRadius(
                roundedRadiusTopLeft = roundedRadiusTopLeft,
                roundedRadiusTopRight = roundedRadiusTopRight,
                roundedRadiusBottomLeft = roundedRadiusBottomLeft,
                roundedRadiusBottomRight = roundedRadiusBottomRight
            ),
            roundedBackgroundColor = roundedBackgroundColor,
            roundedBorderColor = roundedBorderColor,
            roundedBorderSize = roundedBorderSize
        )
    }

    fun setTemporarilyRoundedRadius(
        temporarilyRoundedRadius: RoundedRadius,
        roundedBackgroundColor: Int? = null,
        roundedBorderColor: Int? = null,
        roundedBorderSize: Float? = null
    ) {
        _temporarilyConfig = RoundedViewConfig(
            radius = temporarilyRoundedRadius,
            backgroundColor = roundedBackgroundColor,
            borderColor = roundedBorderColor,
            borderSize = roundedBorderSize
        ).apply {
            if (backgroundColor == null) backgroundColor = _config.backgroundColor
            if (borderColor == null) borderColor = _config.borderColor
            if (borderSize == null) borderSize = _config.borderSize
        }
        roundedView?.invalidate()
    }

    fun onDrawBefore(canvas: Canvas): Boolean {
        if (_temporarilyConfig == null && (!enableRoundedRadius || !_config.hasRadius)) {
            if (_config.backgroundColor != null && _config.backgroundColor != Color.TRANSPARENT) {
                canvas.drawColor(_config.backgroundColor!!)
            }
            return false
        }
        canvas.save()
        roundedView?.clipToOutline = true
        if (_temporarilyConfig != null) {
            drawClipAndBackground(canvas, _temporarilyConfig!!)
            _temporarilyConfig = null
        } else {
            drawClipAndBackground(canvas, _config)
        }
        return true
    }


    fun onDrawAfter(canvas: Canvas) {
        canvas.save()
        if (_temporarilyConfig != null) {
            drawBorder(canvas, _temporarilyConfig!!)
        } else {
            drawBorder(canvas, _config)
        }
    }

    fun drawClipAndBackground(
        canvas: Canvas,
        drawConfig: RoundedViewConfig
    ) {
        val path = Path()
        path.addRoundRect(
            RectF(
                0f,
                0f,
                roundedView!!.width.toFloat(),
                roundedView!!.height.toFloat()
            ),
            drawConfig.getRadii(),
            Path.Direction.CCW
        )
        if (drawConfig.hasBackgroundColor) {
            canvas.drawPath(
                path,
                Paint().apply {
                    color = drawConfig.backgroundColor!!
                    style = Paint.Style.FILL
                }
            )
        }
        canvas.clipPath(path)
    }


    fun drawBorder(
        canvas: Canvas,
        drawConfig: RoundedViewConfig
    ) {
        if (!drawConfig.hasBorder) return
        val offset = drawConfig.borderSize!! / 2
        val path = Path()
        path.addRoundRect(
            RectF(
                offset,
                offset,
                roundedView!!.width.toFloat() - offset,
                roundedView!!.height.toFloat() - offset
            ),
            drawConfig.getRadii(-offset),
            Path.Direction.CCW
        )
        canvas.drawPath(
            path,
            Paint().apply {
                color = drawConfig.borderColor!!
                style = Paint.Style.STROKE
                strokeWidth = drawConfig.borderSize!!
            }
        )
    }
}