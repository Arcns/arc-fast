package com.arc.fast.mask

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import kotlin.math.min

/**
 * 遮罩镂空视图
 */
internal class MaskHollowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 自定义参数
    private var hollowWidthRatio = 0f
    private var hollowHeightRatio = 0f
    private var hollowWidth = 0f
    private var hollowHeight = 0f
    private var hollowDimensionRatio: String? = null
    private var hollowMarginTop = 0f
    private var hollowMarginBottom = 0f
    private var hollowMarginLeft = 0f
    private var hollowMarginRight = 0f
    private var hollowRadius = 0f
    private var hollowBorderSize = 0f
    private var hollowBorderColor = Color.WHITE
    private var hollowBorderRect = 0f
    private var maskBackground = 0x66000000
    private var gravity = Gravity.CENTER

    // 绘制所需参数
    private var isInitCalculate = false
    private val outerFrame by lazy { RoundRect() }
    private val innerFrame by lazy { RoundRect() }
    private val hollowAllBorder by lazy { Path() }
    private val hollowTopLeftBorder by lazy { Path() }
    private val hollowTopRightBorder by lazy { Path() }
    private val hollowBottomLeftBorder by lazy { Path() }
    private val hollowBottomRightBorder by lazy { Path() }
    private val hollowBorderPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val transparentPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    //
    private var isUpdateMask = true
    private var maskBitmap: Bitmap? = null
    private var maskCanvas: Canvas? = null
    private val maskPaint by lazy { Paint() }

    init {
        if (!isInEditMode) {
            setWillNotDraw(false)
            // TypeArray中含有我们需要使用的参数
            val typedArray =
                context.theme.obtainStyledAttributes(attrs, R.styleable.MaskHollowView, 0, 0)
            try {
                hollowWidthRatio = typedArray.getFloat(
                    R.styleable.MaskHollowView_maskHollowView_hollow_width_ratio, 0f
                )
                hollowHeightRatio = typedArray.getFloat(
                    R.styleable.MaskHollowView_maskHollowView_hollow_height_ratio, 0f
                )
                hollowWidth = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_width, 0f
                )
                hollowHeight = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_height, 0f
                )
                hollowDimensionRatio = typedArray.getString(
                    R.styleable.MaskHollowView_maskHollowView_hollow_dimension_ratio
                )
                hollowMarginTop = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_margin_top, 0f
                )
                hollowMarginBottom = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_margin_bottom, 0f
                )
                hollowMarginLeft = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_margin_left, 0f
                )
                hollowMarginRight = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_margin_right, 0f
                )
                hollowRadius = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_radius, 0f
                )
                hollowBorderSize = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_border_size, 0f
                )
                hollowBorderColor = typedArray.getColor(
                    R.styleable.MaskHollowView_maskHollowView_hollow_border_color, Color.WHITE
                )
                hollowBorderRect = typedArray.getDimension(
                    R.styleable.MaskHollowView_maskHollowView_hollow_border_rect, 0f
                )
                maskBackground = typedArray.getColor(
                    R.styleable.MaskHollowView_maskHollowView_mask_background, 0x66000000
                )
                gravity = typedArray.getInt(
                    R.styleable.MaskHollowView_android_gravity, Gravity.CENTER
                )
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if ((!isInitCalculate && width > 0 && height > 0) || (maskBitmap != null && (maskBitmap!!.width != width || maskBitmap!!.height != height))) {
            isInitCalculate = true
            maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                .apply { maskCanvas = Canvas(this) }
            calculateFrame()
            isUpdateMask = true
        }
    }

    fun update(
        hollowWidthRatio: Float? = null,
        hollowHeightRatio: Float? = null,
        hollowWidth: Float? = null,
        hollowHeight: Float? = null,
        hollowDimensionRatio: String? = null,
        hollowMarginTop: Float? = null,
        hollowMarginBottom: Float? = null,
        hollowMarginLeft: Float? = null,
        hollowMarginRight: Float? = null,
        hollowRadius: Float? = null,
        hollowBorderSize: Float? = null,
        hollowBorderColor: Int? = null,
        hollowBorderRect: Float? = null,
        maskBackground: Int? = null,
        gravity: Int? = null
    ) {
        if (hollowWidthRatio != null) this.hollowWidthRatio = hollowWidthRatio
        if (hollowHeightRatio != null) this.hollowHeightRatio = hollowHeightRatio
        if (hollowWidth != null) this.hollowWidth = hollowWidth
        if (hollowHeight != null) this.hollowHeight = hollowHeight
        if (hollowDimensionRatio != null) this.hollowDimensionRatio = hollowDimensionRatio
        if (hollowMarginTop != null) this.hollowMarginTop = hollowMarginTop
        if (hollowMarginBottom != null) this.hollowMarginBottom = hollowMarginBottom
        if (hollowMarginLeft != null) this.hollowMarginLeft = hollowMarginLeft
        if (hollowMarginRight != null) this.hollowMarginRight = hollowMarginRight
        if (hollowRadius != null) this.hollowRadius = hollowRadius
        if (hollowBorderSize != null) this.hollowBorderSize = hollowBorderSize
        if (hollowBorderColor != null) this.hollowBorderColor = hollowBorderColor
        if (hollowBorderRect != null) this.hollowBorderRect = hollowBorderRect
        if (maskBackground != null) this.maskBackground = maskBackground
        if (gravity != null) this.gravity = gravity
        isUpdateMask = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        if (maskCanvas != null && maskBitmap != null) {
            if (isUpdateMask) {
                isUpdateMask = false
                maskCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                maskCanvas?.drawColor(maskBackground)
                outerFrame.onDraw(maskCanvas)
                if (hollowBorderSize > 0f) {
                    if (hollowBorderRect > 0f) {
                        maskCanvas?.drawPath(hollowTopLeftBorder, hollowBorderPaint)
                        maskCanvas?.drawPath(hollowTopRightBorder, hollowBorderPaint)
                        maskCanvas?.drawPath(hollowBottomLeftBorder, hollowBorderPaint)
                        maskCanvas?.drawPath(hollowBottomRightBorder, hollowBorderPaint)
                        innerFrame.onDraw(maskCanvas)
                    } else {
                        maskCanvas?.drawPath(hollowAllBorder, hollowBorderPaint)
                    }
                }
            }
            canvas.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        }
        super.onDraw(canvas)
    }


    private fun calculateFrame() {
        // 按与本控件的比例设置宽高
        if (hollowWidth <= 0f && hollowWidthRatio > 0f) {
            hollowWidth = width * min(1f, hollowWidthRatio)
        }
        if (hollowHeight <= 0f && hollowHeightRatio > 0f) {
            hollowHeight = height * min(1f, hollowHeightRatio)
        }
        // 宽高均未设置时，默认宽度为控件的一半
        if (hollowWidth <= 0 && hollowHeight <= 0) {
            hollowWidth = width * 0.5f
        }
        if (hollowWidth == width.toFloat()) {
            hollowWidth = hollowWidth - hollowMarginLeft - hollowMarginRight
        }
        if (hollowHeight == height.toFloat()) {
            hollowHeight = hollowHeight - hollowMarginTop - hollowMarginBottom
        }
        // 按宽高比例设置
        if (hollowWidth <= 0 || hollowHeight <= 0) {
            var ratio = 1f
            if ((hollowDimensionRatio?.length ?: 0) >= 5
                && (
                        hollowDimensionRatio!!.startsWith("w,", true) ||
                                hollowDimensionRatio!!.startsWith("h,", true)
                        )
            ) {
                val ratioValues = hollowDimensionRatio!!.substring(2).split(":")
                    .mapNotNull {
                        it.toFloatOrNull()?.let { value ->
                            if (value <= 0f) null else value
                        }
                    }
                if (ratioValues.size == 2) {
                    // ratio = w / h
                    ratio = if (hollowDimensionRatio?.startsWith("w,", true) == true) {
                        ratioValues.first() / ratioValues.last()
                    } else {
                        ratioValues.last() / ratioValues.first()
                    }
                }
            }
            if (hollowWidth <= 0) {
                hollowWidth = hollowHeight * ratio
            } else {
                hollowHeight = hollowWidth / ratio
            }
        }
        val centralX = width / 2
        val centralY = height / 2
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val horizontalGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
        val top = when (verticalGravity) {
            Gravity.TOP -> hollowMarginTop
            Gravity.BOTTOM -> height - hollowHeight - hollowMarginBottom
            else -> centralY - hollowHeight / 2 + hollowMarginTop - hollowMarginBottom
        }
        val bottom = top + hollowHeight
        val left = when (horizontalGravity) {
            Gravity.LEFT -> hollowMarginLeft
            Gravity.RIGHT -> width - hollowWidth - hollowMarginRight
            else -> centralX - hollowWidth / 2 + hollowMarginLeft - hollowMarginRight
        }
        val right = left + hollowWidth
        // 最外层镂空
        val outerOffset = if (hollowBorderSize <= 0f || hollowBorderRect <= 0f) 0f
        else hollowBorderSize / 2
        outerFrame.paint = transparentPaint
        outerFrame.radius = hollowRadius - outerOffset
        outerFrame.rect.set(
            left + outerOffset, top + outerOffset, right - outerOffset, bottom - outerOffset
        )
        if (hollowBorderSize > 0f) {
            hollowBorderPaint.color = hollowBorderColor
            hollowBorderPaint.strokeWidth = hollowBorderSize
            if (hollowBorderRect <= 0f) {
                hollowBorderPaint.style = Paint.Style.STROKE
                hollowAllBorder.addRoundRect(
                    RectF(left, top, right, bottom), hollowRadius, hollowRadius, Path.Direction.CCW
                )
            } else {
                // 内层取景框
                hollowBorderPaint.style = Paint.Style.FILL
                innerFrame.paint = transparentPaint
                innerFrame.radius = hollowRadius - hollowBorderSize
                innerFrame.rect.set(
                    left + hollowBorderSize,
                    top + hollowBorderSize,
                    right - hollowBorderSize,
                    bottom - hollowBorderSize
                )
                // 四个边框
                hollowTopLeftBorder.addRoundRect(
                    RectF(left, top, left + hollowBorderRect, top + hollowBorderRect),
                    floatArrayOf(hollowRadius, hollowRadius, 0f, 0f, 0f, 0f, 0f, 0f),
                    Path.Direction.CCW
                )
                hollowTopRightBorder.addRoundRect(
                    RectF(right - hollowBorderRect, top, right, top + hollowBorderRect),
                    floatArrayOf(0f, 0f, hollowRadius, hollowRadius, 0f, 0f, 0f, 0f),
                    Path.Direction.CCW
                )
                hollowBottomLeftBorder.addRoundRect(
                    RectF(
                        left, bottom - hollowBorderRect, left + hollowBorderRect, bottom
                    ),
                    floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, hollowRadius, hollowRadius),
                    Path.Direction.CCW
                )
                hollowBottomRightBorder.addRoundRect(
                    RectF(
                        right - hollowBorderRect, bottom - hollowBorderRect, right, bottom
                    ),
                    floatArrayOf(0f, 0f, 0f, 0f, hollowRadius, hollowRadius, 0f, 0f),
                    Path.Direction.CCW
                )
            }
        }
    }

    internal class RoundRect(
        val rect: RectF = RectF(), var radius: Float = 0f, var paint: Paint? = null
    ) {
        fun onDraw(canvas: Canvas?) {
            if (paint == null || canvas == null) return
            canvas.drawRoundRect(rect, radius, radius, paint!!)
        }

        fun onClip(canvas: Canvas?) {
            if (canvas == null) return
            val path = Path()
            path.addRoundRect(rect, radius, radius, Path.Direction.CW)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                canvas.clipOutPath(path)
            } else {
                canvas.clipPath(path, Region.Op.DIFFERENCE)
            }
        }
    }
}