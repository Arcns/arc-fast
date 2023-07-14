package com.arc.fast.view

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.util.AttributeSet
import android.widget.TextView
import com.arc.fast.textview.R
import com.arc.fast.view.rounded.RoundedTextView

/**
 * 更加方便设置的TextView
 * 1、支持在textview的上下左右设置图片及大小、间距
 * 2、设置设置中粗
 */
open class FastTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RoundedTextView(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode) {
            init(context, attrs)
        }
    }

    var leftImageWidth = 0
    var leftImageHeight = 0
    var leftImagePadding = 0
    var rightImageWidth = 0
    var rightImageHeight = 0
    var rightImagePadding = 0
    var topImageWidth = 0
    var topImageHeight = 0
    var topImagePadding = 0
    var bottomImageHeight = 0
    var bottomImageWidth = 0
    var bottomImagePadding = 0
    var isTextMediumBold = false
        set(value) {
            field = value
            if (value) setTextMediumBold() else disableTextMediumBold()
        }

    /**
     * 初始化读取参数
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray =
            attrs?.let { context.obtainStyledAttributes(it, R.styleable.FastTextView, 0, 0) }
                ?: return
        try {
            leftImageWidth =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_leftImageWidth,
                    0
                )
            leftImageHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_leftImageHeight,
                    0
                )
            leftImagePadding =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_leftImagePadding,
                    0
                )
            rightImageWidth =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_rightImageWidth,
                    0
                )
            rightImageHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_rightImageHeight,
                    0
                )
            rightImagePadding =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_rightImagePadding,
                    0
                )
            topImageWidth =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_topImageWidth,
                    0
                )
            topImageHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_topImageHeight,
                    0
                )
            topImagePadding =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_topImagePadding,
                    0
                )
            bottomImageWidth =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_bottomImageWidth,
                    0
                )
            bottomImageHeight =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_bottomImageHeight,
                    0
                )
            bottomImagePadding =
                typedArray.getDimensionPixelOffset(
                    R.styleable.FastTextView_fastTextView_bottomImagePadding,
                    0
                )
            isTextMediumBold =
                typedArray.getBoolean(R.styleable.FastTextView_fastTextView_textMediumBold, false)
        } finally {
            typedArray.recycle()
        }
        refreshImage()
    }

    /**
     * 重新设置图片大小，并刷新图片
     */
    fun refreshImage() {
        setImages(enableSetSize = true)
    }

    /**
     * 清空设置的所有图片
     */
    fun clearImage() {
        setCompoundDrawables(null, null, null, null)
    }

    /**
     * 设置图片
     */
    fun setImage(
        direction: ImageDirection,
        image: Drawable?,
        imageWidth: Int?,
        imageHeight: Int?,
        imagePadding: Int?
    ) {
        val image = image?.setSize(direction, imageWidth, imageHeight, imagePadding)
        when (direction) {
            ImageDirection.Left -> setImages(leftImage = image)
            ImageDirection.Top -> setImages(topImage = image)
            ImageDirection.Right -> setImages(rightImage = image)
            ImageDirection.Bottom -> setImages(bottomImage = image)
        }
    }

    /**
     * 设置图片
     */
    fun setImage(direction: ImageDirection, drawable: Drawable?) =
        setImage(direction, drawable, null, null, null)


    /**
     * 设置图片
     */
    fun setImage(
        direction: ImageDirection,
        imageRes: Int,
        imageWidth: Int?,
        imageHeight: Int?,
        imagePadding: Int?
    ) =
        setImage(
            direction, try {
                context.getDrawable(imageRes)
            } catch (e: Exception) {
                null
            },
            imageWidth,
            imageHeight,
            imagePadding
        )

    /**
     * 设置图片
     */
    fun setImage(direction: ImageDirection, imageRes: Int) =
        setImage(direction, imageRes, null, null, null)

    /**
     * 修改图片大小（注意该操作不会刷新图片）
     */
    private fun setImageSize(direction: ImageDirection, imageWidth: Int, imageHeight: Int) {
        when (direction) {
            ImageDirection.Left -> {
                leftImageWidth = imageWidth
                leftImageHeight = imageHeight
            }
            ImageDirection.Top -> {
                topImageWidth = imageWidth
                topImageHeight = imageHeight
            }
            ImageDirection.Right -> {
                rightImageWidth = imageWidth
                rightImageHeight = imageHeight
            }
            ImageDirection.Bottom -> {
                bottomImageWidth = imageWidth
                bottomImageHeight = imageHeight
            }
        }
    }

    private fun setImages(
        leftImage: Drawable? = compoundDrawables.getOrNull(0),
        topImage: Drawable? = compoundDrawables.getOrNull(1),
        rightImage: Drawable? = compoundDrawables.getOrNull(2),
        bottomImage: Drawable? = compoundDrawables.getOrNull(3),
        enableSetSize: Boolean = false
    ) {
        setCompoundDrawables(
            leftImage?.let { if (enableSetSize) it.setSize(ImageDirection.Left) else it },
            topImage?.let { if (enableSetSize) it.setSize(ImageDirection.Top) else it },
            rightImage?.let { if (enableSetSize) it.setSize(ImageDirection.Right) else it },
            bottomImage?.let { if (enableSetSize) it.setSize(ImageDirection.Bottom) else it }
        )
    }

    private fun Drawable.setSize(direction: ImageDirection): Drawable {
        var imageWidth = 0
        var imageHeight = 0
        var imagePadding = 0
        when (direction) {
            ImageDirection.Left -> {
                imageWidth = leftImageWidth
                imageHeight = leftImageHeight
                imagePadding = leftImagePadding
            }
            ImageDirection.Top -> {
                imageWidth = topImageWidth
                imageHeight = topImageHeight
                imagePadding = topImagePadding
            }
            ImageDirection.Right -> {
                imageWidth = rightImageWidth
                imageHeight = rightImageHeight
                imagePadding = rightImagePadding
            }
            ImageDirection.Bottom -> {
                imageWidth = bottomImageWidth
                imageHeight = bottomImageHeight
                imagePadding = bottomImagePadding
            }
        }
        return setSize(direction, imageWidth, imageHeight, imagePadding)
    }

    private fun Drawable.setSize(
        direction: ImageDirection,
        imageWidth: Int?,
        imageHeight: Int?,
        imagePadding: Int?,
        isAlsoSaveSize: Boolean = false
    ): Drawable {
        if (imageWidth != null && imageHeight != null && imageWidth > 0 && imageHeight > 0) {
            setBounds(0, 0, imageWidth, imageHeight)
            if (isAlsoSaveSize) {
                setImageSize(direction, imageWidth, imageHeight)
            }
            if (imagePadding != null && imagePadding > 0) {
                var insetLeft = 0
                var insetTop = 0
                var insetRight = 0
                var insetBottom = 0
                when (direction) {
                    ImageDirection.Left -> insetRight = imagePadding
                    ImageDirection.Top -> insetBottom = imagePadding
                    ImageDirection.Right -> insetLeft = imagePadding
                    ImageDirection.Bottom -> insetTop = imagePadding
                }
                return InsetDrawable(this, insetLeft, insetTop, insetRight, insetBottom).apply {
                    setBounds(
                        0,
                        0,
                        imageWidth + insetLeft + insetRight,
                        imageHeight + insetTop + insetBottom
                    )
                }
            }
        } else {
            setSize(direction)
        }
        return this
    }

    enum class ImageDirection {
        Left, Top, Right, Bottom
    }

}

/**
 * 设置文本字体为中粗
 */
fun TextView.setTextMediumBold() = this.setTextMediumBold(1f)
fun TextView.setTextMediumBold(mediumWeight: Float) {
    paint.style = Paint.Style.FILL_AND_STROKE
    paint.strokeWidth = mediumWeight
    invalidate()
}

/**
 * 禁用文本字体中粗
 */
fun TextView.disableTextMediumBold() = setTextMediumBold(0f)