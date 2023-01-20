package com.arc.fast.view.rounded

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr), IRoundedView {

    override var _config = RoundedViewConfig()
    override var _temporarilyConfig: RoundedViewConfig? = null

    init {
        if (attrs != null) initRoundedRadius(context, attrs)
    }

    override fun draw(canvas: Canvas) {
        onDrawBefore(canvas)
        super.draw(canvas)
        onDrawAfter(canvas)
    }
}