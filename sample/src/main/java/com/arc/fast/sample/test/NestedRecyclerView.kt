package com.arc.fast.sample.test

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.math.MathUtils
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView


class NestedRecyclerView(context: Context, attrs: AttributeSet?) :
    RecyclerView(context, attrs), NestedScrollingParent3 {
    private val mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var nestedScrollTarget: View? = null
    private var nestedScrollTargetWasUnableToScroll = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // 没有嵌套滚动目标时不做操作
        if (nestedScrollTarget == null) return super.dispatchTouchEvent(ev)

        // 先禁用拦截事件
        requestDisallowInterceptTouchEvent(true)
        // 执行其他默认操作
        var handled = super.dispatchTouchEvent(ev)
        // 恢复拦截
        requestDisallowInterceptTouchEvent(false)
        if (!handled || nestedScrollTargetWasUnableToScroll) {
            handled = super.dispatchTouchEvent(ev)
        }
        return handled
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        onNestedScrollInternal(dyUnconsumed, type, consumed)
    }

    private fun onNestedScrollInternal(dyUnconsumed: Int, type: Int, consumed: IntArray?) {
        if (dyUnconsumed < 0) scrollDown(dyUnconsumed, consumed)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (axes and View.SCROLL_AXIS_VERTICAL != 0) {
            // 保存嵌套滚动目标
            setTarget(target)
        }
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        setTarget(null)
        mParentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, type, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) scrollUp(dy, consumed)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        if (nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0) {
            // 保存嵌套滚动目标
            setTarget(target)
        }
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View) {
        setTarget(null)
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    /*-------------------------------------------------*/

    private fun scrollDown(dyUnconsumed: Int, consumed: IntArray?) {
        val oldScrollY = scrollY
        scrollBy(0, dyUnconsumed)
        val myConsumed = scrollY - oldScrollY

        if (consumed != null) {
            consumed[1] += myConsumed
        }
    }

    private fun scrollUp(dy: Int, consumed: IntArray) {
        val oldScrollY = scrollY
        scrollBy(0, dy)
        consumed[1] = scrollY - oldScrollY
    }

    override fun scrollTo(x: Int, y: Int) {
        val validY = MathUtils.clamp(y, 0, headerHeight)
        super.scrollTo(x, validY)
    }

    /*--------------------------------------------------------------------------------------------*/

    // 简单把第一个 child 作为 header
    private var headerHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 0) {
            val headView = getChildAt(0)
            measureChildWithMargins(headView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0)
            headerHeight = headView.measuredHeight
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(heightMeasureSpec) + headerHeight,
                    MeasureSpec.EXACTLY
                )
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }


    private fun setTarget(target: View?) {
        nestedScrollTarget = target
        nestedScrollTargetWasUnableToScroll = false
    }
}


open class NestedRecyclerView2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), NestedScrollingParent3 {

    private var nestedScrollTarget: View? = null
    private var nestedScrollTargetWasUnableToScroll = false
    private val parentHelper by lazy { NestedScrollingParentHelper(this) }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // 没有嵌套滚动目标时不做操作
        if (nestedScrollTarget == null) return super.dispatchTouchEvent(ev)

        // 先禁用拦截事件
        requestDisallowInterceptTouchEvent(true)
        // 执行其他默认操作
        var handled = super.dispatchTouchEvent(ev)
        // 恢复拦截
        requestDisallowInterceptTouchEvent(false)
        if (!handled || nestedScrollTargetWasUnableToScroll) {
            handled = super.dispatchTouchEvent(ev)
        }
        return handled
    }

    // 只支持垂直滚动
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int) =
        nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0

    /*  Introduced with NestedScrollingParent2. */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int) =
        onStartNestedScroll(child, target, axes)

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        if (axes and View.SCROLL_AXIS_VERTICAL != 0) {
            // 保存嵌套滚动目标
            setTarget(target)
        }
        parentHelper.onNestedScrollAccepted(child, target, axes)
    }

    /*  Introduced with NestedScrollingParent2. */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (axes and View.SCROLL_AXIS_VERTICAL != 0) {
            // 保存嵌套滚动目标
            setTarget(target)
        }
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(target, dx, dy, consumed)
    }

    /*  Introduced with NestedScrollingParent2. */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        onNestedPreScroll(target, dx, dy, consumed)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null)
    }

    private fun onNestedScrollInternal(dyUnconsumed: Int, type: Int, consumed: IntArray?) {
        if (dyUnconsumed < 0) scrollDown(dyUnconsumed, consumed)
    }

    private fun scrollDown(dyUnconsumed: Int, consumed: IntArray?) {
        val oldScrollY = scrollY
        scrollBy(0, dyUnconsumed)
        val myConsumed = scrollY - oldScrollY

        if (consumed != null) {
            consumed[1] += myConsumed
        }

        //        if (target === nestedScrollTarget && dyUnconsumed != 0) {
//            // 嵌套滚动目标无法完全消耗时，让NestedRecyclerView接管滚动
//            nestedScrollTargetWasUnableToScroll = true
//            // Let the parent start to consume scroll events.
//            target.parent?.requestDisallowInterceptTouchEvent(false)
//        }
    }

    /*  Introduced with NestedScrollingParent2. */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScrollInternal(dyUnconsumed, type, null)
    }

    /*  Introduced with NestedScrollingParent3. */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        onNestedScrollInternal(dyUnconsumed, type, consumed)
    }

    /* From ViewGroup */
    override fun onStopNestedScroll(child: View) {
        // The descendant finished scrolling. Clean up!
        setTarget(null)
        parentHelper.onStopNestedScroll(child)
    }

    /*  Introduced with NestedScrollingParent2. */
    override fun onStopNestedScroll(target: View, type: Int) {
        // The descendant finished scrolling. Clean up!
        setTarget(null)
        parentHelper.onStopNestedScroll(target, type)
    }

    /*  Introduced with NestedScrollingParent2. */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    /* In ViewGroup for API 21+. */
    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ) = super.onNestedFling(target, velocityX, velocityY, consumed)

    private fun setTarget(target: View?) {
        nestedScrollTarget = target
        nestedScrollTargetWasUnableToScroll = false
    }
}