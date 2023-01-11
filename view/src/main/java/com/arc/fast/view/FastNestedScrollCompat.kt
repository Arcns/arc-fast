package com.arc.fast.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * 解决嵌套滚动事件冲突的兼容性宿主
 * 注意：使用时，滚动控件必须是FastNestedScrollCompat直接且唯一的子元素
 */
open class FastNestedScrollCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 需要兼容的滚动方向
     */
    var compatibleOrientation = Orientation.Auto
        set(value) {
            field = value
            actualCompatibleOrientation = value
        }
    private var actualCompatibleOrientation = Orientation.Auto
        get() = checkActualCompatibleOrientation(field)

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
                    Orientation.Auto.value
                ).let {
                    when (it) {
                        Orientation.Horizontal.value -> Orientation.Horizontal
                        Orientation.Vertical.value -> Orientation.Vertical
                        else -> Orientation.Auto
                    }
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    protected open fun checkActualCompatibleOrientation(orientation: Orientation): Orientation {
        if (orientation != Orientation.Auto) return orientation
        val child = child ?: return Orientation.Vertical
        if (child is ScrollView || child is NestedScrollView) {
            return Orientation.Vertical
        } else if (child is HorizontalScrollView) {
            return Orientation.Horizontal
        } else if (child is RecyclerView) {
            when (val layoutManager = child.layoutManager) {
                is LinearLayoutManager -> layoutManager.orientation.toOrientation
                is StaggeredGridLayoutManager -> layoutManager.orientation.toOrientation
                else -> Orientation.Vertical
            }
        } else if (child is ViewPager2) {
            return child.orientation.toOrientation
        }
        return Orientation.Vertical
    }

    private fun canChildScroll(orientation: Orientation, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            Orientation.Horizontal -> child?.canScrollHorizontally(direction) ?: false
            Orientation.Vertical -> child?.canScrollVertically(direction) ?: false
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        // 如果Child不能向与Parent相同的方向滚动，则不需要下一步判断
        val orientation = actualCompatibleOrientation
        if (orientation == Orientation.Auto) return
        if (!canChildScroll(orientation, -1f) &&
            !canChildScroll(orientation, 1f)
        ) return

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            // 事件开始时，禁用所有Parent拦截，以便做判断处理
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
                if ((orientation == Orientation.Horizontal && scaledDy > scaledDx) ||
                    (orientation == Orientation.Vertical && scaledDy < scaledDx)
                ) {
                    // 滚动方向与Parent方向不一致时，允许所有Parent拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                    return
                }
                // 检查Child是否可以向Parent方向滚动
                val isCanChildScroll = canChildScroll(
                    orientation,
                    if (orientation == Orientation.Horizontal) dx else dy
                )
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

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        parent.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    protected val Int.toOrientation: Orientation
        get() = if (this == LinearLayout.HORIZONTAL) Orientation.Horizontal else Orientation.Vertical

    enum class Orientation(val value: Int) {
        Horizontal(LinearLayout.HORIZONTAL), Vertical(LinearLayout.VERTICAL), Auto(LinearLayout.HORIZONTAL + LinearLayout.VERTICAL)

    }
}