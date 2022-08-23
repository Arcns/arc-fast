package com.arc.fast.sample.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.arc.fast.sample.R

class TextViewPlus(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    //左侧图片宽高
    private var leftImgWidth = 0
    private var leftImgHeight = 0
    private var rightImgWidth = 0
    private var rightImgHeight = 0
    private var topImgWidth = 0
    private var topImgHeight = 0
    private var bottomImgHeight = 0
    private var bottomImgWidth = 0

    /**
     * 初始化读取参数
     */
    private fun init(context: Context, attrs: AttributeSet) {
        // TypeArray中含有我们需要使用的参数
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus) ?: return
        // 获得参数个数
        val count: Int = typedArray.indexCount
        var index = 0
        // 遍历参数。先将index从TypedArray中读出来，
        // 得到的这个index对应于attrs.xml中设置的参数名称在R中编译得到的数
        // 这里会得到各个方向的宽和高比例
        for (i in 0 until count) {
            index = typedArray.getIndex(i)
            when (index) {
                R.styleable.TextViewPlus_topImgWidth -> topImgWidth =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_topImgHeight -> topImgHeight =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_bottomImgWidth -> bottomImgWidth =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_bottomImgHeight -> bottomImgHeight =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_leftImgWidth -> leftImgWidth =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_leftImgHeight -> leftImgHeight =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_rightImgWidth -> rightImgWidth =
                    typedArray.getDimensionPixelOffset(index, 0)
                R.styleable.TextViewPlus_rightImgHeight -> rightImgHeight =
                    typedArray.getDimensionPixelOffset(index, 0)
            }
        }
        typedArray.recycle()
        // 获取各个方向的图片，按照：左-上-右-下 的顺序存于数组中
        val drawables = getCompoundDrawables()
        var dir = 0
        // 0-left; 1-top; 2-right; 3-bottom;
        for (drawable in drawables) {
            // 设定图片大小
            setImageSize(drawable, dir++)
        }
        // 将图片放回到TextView中
        setCompoundDrawables(
            drawables[0], drawables[1], drawables[2],
            drawables[3]
        )
    }

    //动态设置TextView图片
    fun setCompoundImg(direction: Int, imgResourceId: Int, width: Int, height: Int) {
        val drawable = try {
            ContextCompat.getDrawable(context, imgResourceId)
        } catch (e: Exception) {
            null
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height)
            when (direction) {
                LEFT_IMG -> setCompoundDrawables(
                    drawable, null, null,
                    null
                )
                TOP_IMG -> setCompoundDrawables(
                    null, drawable, null,
                    null
                )
                RIGHT_IMG -> setCompoundDrawables(
                    null, null, drawable,
                    null
                )
                BOTTOM_IMG -> setCompoundDrawables(
                    null, null, null,
                    drawable
                )
            }
        }
    }

    fun clearCompoundImg() {
        setCompoundDrawables(null, null, null, null)
    }

    //动态设置TextView图片
    fun setCompoundImg(direction: Int, imgResourceId: Int) {
        val drawable = try {
            ContextCompat.getDrawable(context, imgResourceId)
        } catch (e: Exception) {
            null
        }
        if (drawable != null) {
            var imgWidth = 0
            var imgHeight = 0
            when (direction) {
                LEFT_IMG -> {
                    imgWidth = leftImgWidth
                    imgHeight = leftImgHeight
                }
                TOP_IMG -> {
                    imgWidth = topImgWidth
                    imgHeight = topImgHeight
                }
                RIGHT_IMG -> {
                    imgWidth = rightImgWidth
                    imgHeight = rightImgHeight
                }
                BOTTOM_IMG -> {
                    imgWidth = bottomImgWidth
                    imgHeight = bottomImgHeight
                }
            }
            drawable.setBounds(0, 0, imgWidth, imgHeight)
            when (direction) {
                LEFT_IMG -> setCompoundDrawables(
                    drawable, null, null,
                    null
                )
                TOP_IMG -> setCompoundDrawables(
                    null, drawable, null,
                    null
                )
                RIGHT_IMG -> setCompoundDrawables(
                    null, null, drawable,
                    null
                )
                BOTTOM_IMG -> setCompoundDrawables(
                    null, null, null,
                    drawable
                )
            }
        }
    }

    /**
     * 设定图片的大小
     */
    private fun setImageSize(d: Drawable?, dir: Int) {
        if (d == null) {
            return
        }
        var imgWidth = 0
        var imgHeight = 0
        when (dir) {
            LEFT_IMG -> {
                imgWidth = leftImgWidth
                imgHeight = leftImgHeight
            }
            TOP_IMG -> {
                imgWidth = topImgWidth
                imgHeight = topImgHeight
            }
            RIGHT_IMG -> {
                imgWidth = rightImgWidth
                imgHeight = rightImgHeight
            }
            BOTTOM_IMG -> {
                imgWidth = bottomImgWidth
                imgHeight = bottomImgHeight
            }
        }
        // 如果有某个方向的宽或者高没有设定值，则不去设定图片大小
        if (imgWidth != 0 && imgHeight != 0) {
            d.setBounds(0, 0, imgWidth, imgHeight)
        }
    }

    companion object {
        const val LEFT_IMG = 0
        const val TOP_IMG = 1
        const val RIGHT_IMG = 2
        const val BOTTOM_IMG = 3
    }

    init {
        if (!isInEditMode) {
            init(context, attrs)
        }
    }
}