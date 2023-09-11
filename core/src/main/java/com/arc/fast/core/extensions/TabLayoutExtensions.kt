package com.arc.fast.core.extensions

import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.tabs.TabLayout

/**
 * 禁用TabLayout的长按提示
 */
fun TabLayout.disabledTooltipText() {
    for (index in 0 until tabCount) {
        TooltipCompat.setTooltipText(getTabAt(index)?.view ?: return, null)
    }
}


/**
 * 循环输出Tab
 */
fun TabLayout.forEach(action: (TabLayout.Tab) -> Unit) {
    val count = tabCount
    for (i in 0 until count) {
        action(getTabAt(i) ?: return)
    }
}

/**
 * 循环输出Tab
 */
fun TabLayout.forEachIndexed(action: (Int, TabLayout.Tab) -> Unit) {
    val count = tabCount
    for (i in 0 until count) {
        action(i, getTabAt(i) ?: return)
    }
}

/**
 * 添加TabLayout选中回调
 */
fun TabLayout.addOnTabSelectedCallback(onTabSelected: (tab: TabLayout.Tab, isSelected: Boolean) -> Unit): TabLayout.OnTabSelectedListener {
    return object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) = onTabSelected(tab, true)

        override fun onTabUnselected(tab: TabLayout.Tab) = onTabSelected(tab, false)

        override fun onTabReselected(tab: TabLayout.Tab) = onTabSelected(tab, true)
    }.apply {
        addOnTabSelectedListener(this)
    }
}