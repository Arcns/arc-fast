package com.arc.fast.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.youth.banner.Banner

/**
 * 解决Banner嵌套滚动事件冲突的兼容性宿主
 * 注意：使用时Banner需要是此宿主布局的直接且唯一的子元素
 */
open class FastBannerNestedScrollCompat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FastNestedScrollCompat(context, attrs, defStyleAttr) {

    override val child: View?
        get() = if (childCount > 0) getChildAt(0).let {
            if (it is Banner<*, *>) {
                it.setIntercept(false)
                it.viewPager2
            } else it
        } else null
}