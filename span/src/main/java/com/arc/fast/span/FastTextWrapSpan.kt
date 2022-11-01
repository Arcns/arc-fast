package com.arc.fast.span

import android.graphics.*
import android.text.style.ReplacementSpan

/**
 * 可快速实现背景,边距,圆角包裹的文本Span
 * 注意:由于ReplacementSpan的限制,该Span无法实现自动换行
 */
open class FastTextWrapSpan(
    private val radius: Float = 0f,
    private val borderSize: Float = 0f,
    private val borderColor: Int = Color.TRANSPARENT,
    private val backgroundColor: Int = Color.TRANSPARENT,
    private val textSize: Float? = null,
    private val textColor: Int? = null,
    private val textStyle: Int? = null,
    private val textLeftMargin: Float = 0f,
    private val textRightMargin: Float = 0f,
    private val leftPadding: Float = 0f,
    private val topPadding: Float = 0f,
    private val rightPadding: Float = 0f,
    private val bottomPadding: Float = 0f
) : ReplacementSpan() {
    private var textWidth = 0
    private var defaultTextSize = 0f
    private var defaultTextTypeface = Typeface.DEFAULT
    private var defaultColor = Color.BLACK
    private var defaultStrokeWidth = 0f
    private val strokeWidthOffset: Float by lazy { borderSize / 2 }


    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        if (defaultTextSize == 0f) {
            defaultTextSize = paint.textSize
            defaultColor = paint.color
            defaultStrokeWidth = paint.strokeWidth
            defaultTextTypeface = paint.typeface
        }
        if (textSize != null) paint.textSize = textSize
        if (textStyle != null) paint.typeface = Typeface.create(defaultTextTypeface, textStyle)
        textWidth = (paint.measureText(text, start, end) + leftPadding + rightPadding).toInt()
        return textWidth + textLeftMargin.toInt() + textRightMargin.toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        //绘制圆角矩形
        val metrics = paint.fontMetrics;
        val top = y + metrics.top
        val bottom = y + metrics.bottom
        paint.typeface = Typeface.DEFAULT
        val rectF = RectF(
            x + strokeWidthOffset + textLeftMargin,
            top + strokeWidthOffset - topPadding - bottomPadding,
            x - strokeWidthOffset + textLeftMargin + textWidth,
            bottom - strokeWidthOffset
        )
        // 绘制背景
        if (backgroundColor != Color.TRANSPARENT) {
            paint.color = backgroundColor
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }

        // 绘制边框
        if (borderSize > 0f && borderColor != Color.TRANSPARENT) {
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize
            paint.isAntiAlias = true

            //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
            // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
            //x+2.5f解决线条粗细不一致问题
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }

        //绘制文字
        paint.textSize = textSize ?: defaultTextSize
        paint.color = textColor ?: defaultColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = defaultStrokeWidth
        canvas.drawText(
            text,
            start,
            end,
            x + textLeftMargin + leftPadding,
            y.toFloat() - topPadding,
            paint
        )
        //恢复画笔
        paint.typeface = defaultTextTypeface
        paint.color = defaultColor
        paint.textSize = defaultTextSize
    }
}