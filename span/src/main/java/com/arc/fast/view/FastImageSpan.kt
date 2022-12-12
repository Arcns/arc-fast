package com.arc.fast.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

class FastImageSpan(
    private val mDrawable: Drawable,
    width: Int,
    height: Int = width,
    private val leftMargin: Int = 0,
    private val rightMargin: Int = 0
) : ImageSpan(mDrawable) {

    constructor(
        res: Resources,
        bitmap: Bitmap,
        width: Int,
        height: Int = width,
        leftMargin: Int = 0,
        rightMargin: Int = 0,
    ) : this(BitmapDrawable(res, bitmap), width, height, leftMargin, rightMargin)

    constructor(
        context: Context,
        res: Int,
        width: Int,
        height: Int = width,
        leftMargin: Int = 0,
        rightMargin: Int = 0,
    ) : this(context.getDrawable(res)!!, width, height, leftMargin, rightMargin)


    init {
        mDrawable.setBounds(0, 0, width, height)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fontMetricsInt: FontMetricsInt?
    ): Int {
        val drawable = drawable ?: mDrawable
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
        return rect.right + leftMargin + rightMargin
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
        val transX = x + leftMargin
        val transY = (bottom - top - drawable.bounds.bottom) / 2 + top
        canvas.translate(transX, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}
