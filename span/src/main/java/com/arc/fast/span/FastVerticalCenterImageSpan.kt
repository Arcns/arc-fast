package com.arc.fast.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

/**
 * 支持与文字垂直居中的图片span
 */
class FastVerticalCenterImageSpan(drawable: Drawable) : ImageSpan(drawable) {
//    override fun draw(
//        canvas: Canvas,
//        text: CharSequence?,
//        start: Int,
//        end: Int,
//        x: Float,
//        top: Int,
//        y: Int,
//        bottom: Int,
//        paint: Paint
//    ) {
//        val b = drawable
//        val fm = paint.fontMetricsInt
//        val textHeigth = fm.bottom - fm.top
//        val drawableHeight = b.bounds.bottom
//        if (textHeigth > drawableHeight) {
//            val transY = ((y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2)
//            canvas.save()
//            canvas.translate(x, transY.toFloat())
//            b.draw(canvas)
//            canvas.restore()
//        } else {
//            super.draw(canvas, text, start, end, x, top, y, bottom, paint)
//        }
//    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fontMetricsInt: Paint.FontMetricsInt?
    ): Int {
        val drawable = drawable
        val rect = drawable.bounds
        if (fontMetricsInt != null) {
            val fmPaint = paint.fontMetricsInt
            val fontHeight = fmPaint.bottom - fmPaint.top
            val drHeight = rect.bottom - rect.top
            val top = drHeight / 2 - fontHeight / 4
            val bottom = drHeight / 2 + fontHeight / 4
            fontMetricsInt.ascent = -bottom
            fontMetricsInt.top = -bottom
            fontMetricsInt.bottom = top
            fontMetricsInt.descent = top
        }
        return rect.right
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
        val drawable = getDrawable()
        canvas.save()
        val transX = x
        val transY = (bottom - top - drawable.bounds.bottom) / 2 + top
        canvas.translate(transX, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}