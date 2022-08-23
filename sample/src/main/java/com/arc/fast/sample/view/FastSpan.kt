package com.arc.fast.sample.view

import android.graphics.*
import android.graphics.Paint.FontMetricsInt
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import com.arc.fast.core.extensions.color
import com.arc.fast.core.extensions.dp
import com.arc.fast.core.extensions.sp
import com.arc.fast.sample.R

/**
 * 快速设置Span
 */
fun SpannableStringBuilder.append(
    text: CharSequence,
    span: ReplacementSpan
): SpannableStringBuilder {
    val start = length
    val end = start + text.length
    append(text)
    setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    return this
}

/**
 * 圆角包裹的Span
 */
open class RoundBorderWrapSpan(
    private val borderRadius: Float,
    private val borderSize: Float,
    private val borderColor: Int,
    private val textSize: Float,
    private val textColor: Int,
    private val textRightMargin: Int,
    private val textRightLeft: Int,
    private val leftPadding: Float,
    private val topPadding: Float,
    private val rightPadding: Float,
    private val bottomPadding: Float
) : ReplacementSpan() {
    private var textWidth = 0
    private var defaultTextSize = 0f
    private var defaultColor = Color.BLACK
    private var defaultStrokeWidth = 0f
    private var defaultTypeface = Typeface.DEFAULT_BOLD
    private val strokeWidthOffset: Float by lazy { borderSize / 2 }


    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        if (defaultTextSize == 0f) {
            defaultTextSize = paint.textSize
            defaultColor = paint.color
            defaultStrokeWidth = paint.strokeWidth
            defaultTypeface = paint.typeface
        }
        paint.textSize = textSize
        textWidth = (paint.measureText(text, start, end) + leftPadding + rightPadding).toInt()
        return textRightLeft + textWidth + textRightMargin  //5:距离其他文字的空白
    }

    /**
     * @param y baseline
     */
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int, bottom: Int,
        paint: Paint
    ) {
        paint.textSize = textSize
        //绘制圆角矩形
        val metrics = paint.fontMetrics;
        val top = y + metrics.top
        val bottom = y + metrics.bottom
        paint.typeface = Typeface.DEFAULT
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        paint.isAntiAlias = true
        val rectF = RectF(
            x + textRightLeft + strokeWidthOffset,
            top + strokeWidthOffset - topPadding - bottomPadding,
            x + textRightLeft - strokeWidthOffset + textWidth,
            bottom - strokeWidthOffset
        )
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。
        // paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        //x+2.5f解决线条粗细不一致问题
        canvas.drawRoundRect(rectF, borderRadius, borderRadius, paint)

        //绘制文字
        paint.color = textColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = defaultStrokeWidth
        canvas.drawText(
            text,
            start,
            end,
            x + textRightLeft + leftPadding,
            y.toFloat() - topPadding,
            paint
        )
        //恢复画笔
        paint.typeface = defaultTypeface
        paint.color = defaultColor
        paint.textSize = defaultTextSize
    }
}

class NewMenuWrapSpan : RoundBorderWrapSpan(
    borderRadius = 4f.dp,
    borderSize = 1f.dp,
    borderColor = R.color.main.color,
    textSize = 12f.sp,
    textColor = R.color.main.color,
    textRightLeft = 8.dp,
    textRightMargin = 0.dp,
    leftPadding = 4f.dp,
    topPadding = 2f.dp,
    rightPadding = 4f.dp,
    bottomPadding = 2f.dp
)
