package com.arc.fast.core.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.arc.fast.core.R

class FastTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    var drawableLeftWidth = 0
    var drawableLeftHeight = 0
    var drawableRightWidth = 0
    var drawableRightHeight = 0
    var drawableTopWidth = 0
    var drawableTopHeight = 0
    var drawableBottomHeight = 0
    var drawableBottomWidth = 0

    init {
        if (!isInEditMode) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastTextView)
            val count: Int = typedArray.indexCount
            for (i in 0 until count) {
                when (val index = typedArray.getIndex(i)) {
                    R.styleable.FastTextView_drawableTopWidth -> drawableTopWidth =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableTopHeight -> drawableTopHeight =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableBottomWidth -> drawableBottomWidth =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableBottomHeight -> drawableBottomHeight =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableLeftWidth -> drawableLeftWidth =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableLeftHeight -> drawableLeftHeight =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableRightWidth -> drawableRightWidth =
                        typedArray.getDimensionPixelOffset(index, 0)
                    R.styleable.FastTextView_drawableRightHeight -> drawableRightHeight =
                        typedArray.getDimensionPixelOffset(index, 0)
                }
            }
            typedArray.recycle()
            // 获取各个方向的图片，按照：左-上-右-下 的顺序存于数组中
            val drawables = compoundDrawables
            // 将图片放回到TextView中
            setDrawables(
                drawables[0],
                drawables[1],
                drawables[2],
                drawables[3]
            )
        }
    }

    fun setDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        setCompoundDrawables(
            left?.setDrawableSize(drawableLeftWidth, drawableLeftHeight),
            top?.setDrawableSize(drawableTopWidth, drawableTopHeight),
            right?.setDrawableSize(drawableRightWidth, drawableRightHeight),
            bottom?.setDrawableSize(drawableBottomWidth, drawableBottomHeight)
        )
    }

    fun clearDrawables() {
        setCompoundDrawables(null, null, null, null)
    }

    fun setDrawableLeft(drawable: Drawable?, width: Int? = null, height: Int? = null) {
        val drawables = compoundDrawables
        setCompoundDrawables(
            drawable?.setDrawableSize(width ?: drawableLeftWidth, height ?: drawableLeftHeight),
            drawables[1],
            drawables[2],
            drawables[3]
        )
    }

    fun setDrawableTop(drawable: Drawable?, width: Int? = null, height: Int? = null) {
        val drawables = compoundDrawables
        setCompoundDrawables(
            drawables[0],
            drawable?.setDrawableSize(width ?: drawableTopWidth, height ?: drawableTopHeight),
            drawables[2],
            drawables[3]
        )
    }

    fun setDrawableRight(drawable: Drawable?, width: Int? = null, height: Int? = null) {
        val drawables = compoundDrawables
        setCompoundDrawables(
            drawables[0],
            drawables[1],
            drawable?.setDrawableSize(width ?: drawableRightWidth, height ?: drawableRightHeight),
            drawables[3]
        )
    }

    fun setDrawableBottom(drawable: Drawable?, width: Int? = null, height: Int? = null) {
        val drawables = compoundDrawables
        setCompoundDrawables(
            drawables[0],
            drawables[1],
            drawables[2],
            drawable?.setDrawableSize(width ?: drawableBottomWidth, height ?: drawableBottomHeight)
        )
    }


    /**
     * 设定图片的大小
     */
    private fun Drawable.setDrawableSize(width: Int, height: Int): Drawable? {
        // 如果有某个方向的宽或者高没有设定值，则不去设定图片大小
        if (width > 0 && height > 0) {
            this.setBounds(0, 0, width, height)
        }
        return this
    }

}