package com.arc.fast.sample.view.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.arc.fast.sample.R
import kotlin.math.abs

/**
 * 内层的RecyclerView
 */
class ChildRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseRecyclerView(context, attrs, defStyleAttr) {

    private var parentRecyclerView: ParentRecyclerView? = null

    private val mTouchSlop: Int
    private var downX: Float = 0f
    private var downY: Float = 0f

    private var dragState: Int = DRAG_IDLE

    companion object {
        private const val DRAG_IDLE = 0
        private const val DRAG_VERTICAL = 1
        private const val DRAG_HORIZONTAL = 2
    }

    init {
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
    }

    /**
     * 当整个childRecyclerView被detach之后，及时上报parentRecyclerView
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        val velocityY = getVelocityY()
        parentRecyclerView?.fling(0, -velocityY)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // ACTION_DOWN 触摸按下，保存临时变量
            dragState = DRAG_IDLE
            downX = ev.rawX
            downY = ev.rawY
            this.stopFling()

            // 一上来就禁止ParentRecyclerView拦截Touch事件
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            this.formDragState(ev)
            if (dragState == DRAG_VERTICAL) {
                // 水平滑动，直接拦截
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 这段逻辑主要是RecyclerView最底部，垂直上拉后居然还能左右滑动，不能忍
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            this.formDragState(ev)
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 定性dragState
     */
    private fun formDragState(ev: MotionEvent) {
        if (dragState == DRAG_IDLE) {
            val xDistance = abs(ev.rawX - downX)
            val yDistance = abs(ev.rawY - downY)

            if (xDistance > yDistance && xDistance > mTouchSlop) {
                // 水平滑动
                dragState = DRAG_HORIZONTAL

                // touch事件允许 ViewPager / ViewPager2 处理
                parent.requestDisallowInterceptTouchEvent(false)
            } else if (yDistance > xDistance && yDistance > mTouchSlop) {
                // 垂直滑动
                dragState = DRAG_VERTICAL
            }
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        connectToParent()
    }

    /**
     * 跟ParentView建立连接，主要两件事情 -
     * 1. 将自己上报ViewPager/ViewPager2，通过tag关联到currentItem的View中
     * 2. 将ViewPager/ViewPager2报告给ParentRecyclerView
     * 这一坨代码需要跟ParentRecyclerView连起来看，否则可能会懵
     */
    private fun connectToParent() {
        var viewPager: ViewPager? = null
        var viewPager2: ViewPager2? = null
        var lastTraverseView: View = this

        var parentView = this.parent as View
        while (parentView != null) {
            val parentClassName = parentView::class.java.canonicalName
            if ("androidx.viewpager2.widget.ViewPager2.RecyclerViewImpl" == parentClassName) {
                // 使用ViewPager2，parentView的顺序如下:
                // ChildRecyclerView -> 若干View -> FrameLayout -> RecyclerViewImpl -> ViewPager2 -> 若干View -> ParentRecyclerView

                // 此时lastTraverseView是上方注释中的FrameLayout，算是"ViewPager2.child"，我们此处将ChildRecyclerView设置到FrameLayout的tag中
                // 这个tag会在ParentRecyclerView中用到
                lastTraverseView.setTag(R.id.tag_saved_child_recycler_view, this)
            } else if (parentView is ViewPager) {
                // 使用ViewPager，parentView顺序如下：
                // ChildRecyclerView -> 若干View -> ViewPager -> 若干View -> ParentRecyclerView
                // 此处将ChildRecyclerView保存到ViewPager最直接的子View中
                if (lastTraverseView != this) {
                    // 这个tag会在ParentRecyclerView中用到
                    lastTraverseView.setTag(R.id.tag_saved_child_recycler_view, this)
                }

                // 碰到ViewPager，需要上报给ParentRecyclerView
                viewPager = parentView
            } else if (parentView is ViewPager2) {
                // 碰到ViewPager2，需要上报给ParentRecyclerView
                viewPager2 = parentView
            } else if (parentView is ParentRecyclerView) {
                // 碰到ParentRecyclerView，设置结束
                parentView.setInnerViewPager(viewPager)
                parentView.setInnerViewPager2(viewPager2)
                parentView.setChildPagerContainer(lastTraverseView)
                this.parentRecyclerView = parentView
                return
            }

            lastTraverseView = parentView
            parentView = parentView.parent as View
        }
    }

//    override fun onScrollStateChanged(state: Int) {
//        super.onScrollStateChanged(state)
//
//        if (state == SCROLL_STATE_IDLE) {
//            val velocityY = getVelocityY()
//            // 滑动到最底部时，骤然停止，这时需要把速率传递给parentRecyclerView
//            parentRecyclerView?.fling(0, velocityY)
//        }
//    }
}
