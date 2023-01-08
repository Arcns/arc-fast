package com.arc.fast.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * 解决嵌套滚动事件冲突的兼容性宿主
 * 注意：使用时可滚动元素需要是此宿主布局的直接且唯一的子元素
 */
open class FastNestedScrollCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 需要兼容的滚动方向
     */
    var compatibleOrientation = Orientation.Horizontal

    //
    private val scaledTouchSlop by lazy {
        ViewConfiguration.get(context).scaledTouchSlop
    }
    private var initialX = 0f
    private var initialY = 0f

    open val child: View? get() = if (childCount > 0) getChildAt(0) else null

    init {
        if (!isInEditMode) {
            setWillNotDraw(false)
            // TypeArray中含有我们需要使用的参数
            val typedArray =
                context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.FastNestedScrollableHost,
                    0,
                    0
                )
            try {
                compatibleOrientation = typedArray.getInt(
                    R.styleable.FastNestedScrollableHost_fastNestedScrollableHost_orientation,
                    Orientation.Horizontal.value
                ).let {
                    when (it) {
                        Orientation.Horizontal.value -> Orientation.Horizontal
                        Orientation.Vertical.value -> Orientation.Vertical
                        else -> Orientation.All
                    }
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            0 -> child?.canScrollHorizontally(direction) ?: false
            1 -> child?.canScrollVertically(direction) ?: false
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
        Log.e("aaaaaa", "onTouchEvent")
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
        Log.e("aaaaaa", "dispatchTouchEvent")
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        Log.e("aaaaaa", "handleInterceptTouchEvent")
        // 如果Child不能向与Parent相同的方向滚动，则不需要下一步判断
        if (compatibleOrientation != Orientation.All &&
            !canChildScroll(compatibleOrientation.value, -1f) &&
            !canChildScroll(compatibleOrientation.value, 1f)
        ) return

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY

            // 根据方向设置触摸斜率
            val scaledDx =
                dx.absoluteValue// * if (compatibleOrientation == Orientation.Horizontal) 0.5f else 1f
            val scaledDy =
                dy.absoluteValue// * if (compatibleOrientation == Orientation.Vertical) 0.5f else 1f

            if (scaledDx > scaledTouchSlop || scaledDy > scaledTouchSlop) {
                if ((compatibleOrientation == Orientation.Horizontal && scaledDy > scaledDx) ||
                    (compatibleOrientation == Orientation.Vertical && scaledDy < scaledDx)
                ) {
                    // 滚动方向与Parent方向不一致时，允许所有Parent拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                    return
                }
                // 检查Child是否可以向Parent方向滚动
                val isCanChildScroll = if (compatibleOrientation != Orientation.All)
                    canChildScroll(
                        compatibleOrientation.value,
                        if (compatibleOrientation == Orientation.Horizontal) dx else dy
                    )
                else canChildScroll(Orientation.Horizontal.value, dx) ||
                        canChildScroll(Orientation.Vertical.value, dy)
                if (isCanChildScroll) {
                    // Child可以滚动时，不允许所有Parent拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    // Child不可以滚动时, 允许所有Parent拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
    }

    enum class Orientation(val value: Int) {
        Horizontal(LinearLayout.HORIZONTAL), Vertical(LinearLayout.VERTICAL), All(LinearLayout.HORIZONTAL + LinearLayout.VERTICAL)
    }
}