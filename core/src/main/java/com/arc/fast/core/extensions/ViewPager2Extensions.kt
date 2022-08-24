package com.arc.fast.core.extensions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// 禁用TabLayout的长按提示
fun TabLayout.disabledTooltipText() {
    for (index in 0 until tabCount) {
        TooltipCompat.setTooltipText(getTabAt(index)?.view ?: return, null)
    }
}

// 绑定ViewPager2与TabLayout
fun <Data> TabLayout.bindToViewPager2(
    activity: AppCompatActivity,
    viewPager: ViewPager2,
    items: List<ViewPager2FragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPager2FragmentItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) = this.bindToViewPager2(
    activity.supportFragmentManager,
    activity.lifecycle,
    viewPager,
    items,
    customView,
    currentItemId,
    offscreenPageLimit,
    onCreateFragment
)

// 绑定ViewPager2与TabLayout
fun <Data> TabLayout.bindToViewPager2(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    viewPager: ViewPager2,
    items: List<ViewPager2FragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPager2FragmentItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) {
    viewPager.offscreenPageLimit = offscreenPageLimit
    viewPager.adapter = ViewPager2FragmentAdapter(
        fragmentManager, lifecycle, items, onCreateFragment
    )
    val tabLayoutMediator =
        TabLayoutMediator(
            this,
            viewPager
        ) { tab, position ->
            if (customView != null) customView.invoke(tab, items[position], position)
            else tab.text = items[position].title
        }
    tabLayoutMediator.attach()
    disabledTooltipText()
    var selectedId = currentItemId ?: items.firstOrNull { it.selected == true }?.id
    if (!selectedId.isNullOrBlank()) {
        val selectTab = items.indexOfFirst { it.id == selectedId }
        if (selectTab > 0) {
            getTabAt(selectTab)?.select()
        }
    }
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            lifecycle.removeObserver(this)
            viewPager.removeAllViews()
            tabLayoutMediator.detach()
            super.onDestroy(owner)
        }
    })
}

data class ViewPager2FragmentItem<Data>(
    val id: String,
    val title: String,
    val data: Data? = null,
    val args: Bundle? = null,
    val selected: Boolean? = null,
)


class ViewPager2FragmentAdapter<Data>(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val items: List<ViewPager2FragmentItem<Data>>,
    private val onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position].let {
        onCreateFragment(position, it).apply {
            arguments = (arguments ?: Bundle()).apply {
                if (it.args?.isEmpty == false)
                    putAll(it.args)
            }
        }
    }
}