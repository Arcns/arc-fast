package com.arc.fast.view.rounded

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TableLayout


open class RoundedTableLayout : TableLayout, IRoundedView {
    override var _config = RoundedViewConfig()
    override var _temporarilyConfig: RoundedViewConfig? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
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