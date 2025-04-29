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
 * 扩展版：除了支持NestedScrollView、ScrollView、ViewPager等Android基础控件，
 * 还支持RecyclerView、ViewPager2
 */
open class FastNestedScrollCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FastBaseNestedScrollCompat(context, attrs, defStyleAttr) {

    /**
     * 获取实际需要兼容的滑动方向
     */
    protected override fun checkActualCompatibleOrientation(orientation: Orientation): Orientation {
        if (orientation != Orientation.Auto) return orientation
        val child = child ?: return Orientation.Vertical
        if (child is RecyclerView) {
            return when (val layoutManager = child.layoutManager) {
                is LinearLayoutManager -> layoutManager.orientation.toOrientation
                is StaggeredGridLayoutManager -> layoutManager.orientation.toOrientation
                else -> Orientation.Vertical
            }
        } else if (child is ViewPager2) {
            return child.orientation.toOrientation
        }
        return super.checkActualCompatibleOrientation(orientation)
    }
}