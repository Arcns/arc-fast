package com.arc.fast.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.youth.banner.Banner

/**
 * 解决Banner嵌套滚动事件冲突的兼容性宿主
 * 注意：使用时，滚动控件必须是FastNestedScrollCompat直接且唯一的子元素
 * Banner版：除了支持NestedScrollView、ScrollView、ViewPager、RecyclerView、ViewPager2，
 * 还支持Banner
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