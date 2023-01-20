package com.arc.fast.view

import android.content.Context
import android.util.AttributeSet
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
import com.arc.fast.nestedscrollcompat.R
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

    // 拦截处理结果
    private var handleInterceptResult = HandleInterceptState.Wait

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

    /**
     * 获取实际需要兼容的滑动方向
     */
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

    /**
     * 是否可以往指定方向进行滑动
     */
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
        // 取消事件
        if (e.action == MotionEvent.ACTION_CANCEL) {
            handleInterceptResult = HandleInterceptState.Wait
            return
        }
        // 如果Child不能向与Parent相同的方向滚动，则不需要下一步判断
        val orientation = actualCompatibleOrientation
        if (orientation == Orientation.Auto) return
        if (!canChildScroll(orientation, -1f) &&
            !canChildScroll(orientation, 1f)
        ) {
            return
        }
        // 开始检查判断
        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            // 事件开始时，先禁用所有Parent拦截，以便做判断处理
            parent.requestDisallowInterceptTouchEvent(true)
            handleInterceptResult = HandleInterceptState.Ready
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            if (handleInterceptResult == HandleInterceptState.Ready)
                handleInterceptResult = HandleInterceptState.Check
            val dx = e.x - initialX
            val dy = e.y - initialY
            // 根据方向设置触摸斜率
            val scaledDx =
                dx.absoluteValue// * if (compatibleOrientation == Orientation.Horizontal) 0.5f else 1f
            val scaledDy =
                dy.absoluteValue// * if (compatibleOrientation == Orientation.Vertical) 0.5f else 1f
            // 判断是否达到滑动的阙值
            if (scaledDx > scaledTouchSlop || scaledDy > scaledTouchSlop) {
                if ((orientation == Orientation.Horizontal && scaledDy > scaledDx) ||
                    (orientation == Orientation.Vertical && scaledDy < scaledDx)
                ) {
                    // 滚动方向与兼容方向不一致时，允许所有Parent拦截
                    handleInterceptResult = HandleInterceptState.Allow
                    parent.requestDisallowInterceptTouchEvent(false)
                    return
                }
                // 检查Child是否可以向兼容方向滚动
                val isCanChildScroll = canChildScroll(
                    orientation,
                    if (orientation == Orientation.Horizontal) dx else dy
                )
                if (isCanChildScroll) {
                    // Child可以滚动时，禁用所有Parent拦截
                    handleInterceptResult = HandleInterceptState.Disallow
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    // Child不可以滚动时, 允许所有Parent拦截
                    handleInterceptResult = HandleInterceptState.Allow
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (!disallowIntercept && handleInterceptResult == HandleInterceptState.Disallow) {
            // 如果处理状态是”禁用拦截“，则无法再允许拦截
            return
        }
        if (!disallowIntercept && handleInterceptResult == HandleInterceptState.Ready) {
            // 如果处理状态是”准备处理“，而程序通知”允许拦截“时，则本视图”允许拦截“，但Parent仍然”禁止拦截“（以便做判断处理）
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
            parent.requestDisallowInterceptTouchEvent(true)
            return
        }
        if (child is ScrollView || child is NestedScrollView || child is HorizontalScrollView) {
            // ScrollView会默认”禁用拦截“，因此需要在此进行特殊处理
            parent.requestDisallowInterceptTouchEvent(disallowIntercept)
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }
    }

    protected val Int.toOrientation: Orientation
        get() = if (this == LinearLayout.HORIZONTAL) Orientation.Horizontal else Orientation.Vertical

    enum class Orientation(val value: Int) {
        Horizontal(LinearLayout.HORIZONTAL), Vertical(LinearLayout.VERTICAL), Auto(3)
    }

    enum class HandleInterceptState {
        // 等待处理
        Wait,

        // 准备处理：已执行ACTION_DOWN（未执行ACTION_MOVE）
        Ready,

        // 处理中：已执行ACTION_MOVE，开始进行处理
        Check,

        // 处理完成：允许拦截
        Allow,

        // 处理完成：禁止拦截
        Disallow
    }
}