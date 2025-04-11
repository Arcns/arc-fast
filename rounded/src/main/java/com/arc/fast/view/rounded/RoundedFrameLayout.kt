package com.arc.fast.view.rounded

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout


open class RoundedFrameLayout : FrameLayout, IRoundedView {
    override var _config = RoundedViewConfig()
    override var _temporarilyConfig: RoundedViewConfig? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initRoundedRadius(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initRoundedRadius(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initRoundedRadius(context, attrs)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        layoutWidth = measuredWidth
//        layoutHeight = measuredHeight
//        setMeasuredDimension(layoutWidth, layoutHeight)
//    }

    override fun draw(canvas: Canvas) {
        onDrawBefore(canvas)
        super.draw(canvas)
        onDrawAfter(canvas)
    }
}