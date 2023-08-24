package com.arc.fast.core.extensions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
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
    lifecycle: Lifecycle = activity.lifecycle,
    viewPager: ViewPager2,
    items: List<ViewPager2FragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPager2Item<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    isEnableScrollPage: Boolean = true,
    isSmoothScroll: Boolean = true,
    onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) = this.bindToViewPager2(
    activity.supportFragmentManager,
    lifecycle,
    viewPager,
    items,
    customView,
    currentItemId,
    offscreenPageLimit,
    isEnableScrollPage,
    isSmoothScroll,
    onCreateFragment
)

// 绑定ViewPager2与TabLayout
fun <Data> TabLayout.bindToViewPager2(
    fragment: Fragment,
    lifecycle: Lifecycle = fragment.viewLifecycleOwner.lifecycle,
    viewPager: ViewPager2,
    items: List<ViewPager2FragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPager2Item<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    isEnableScrollPage: Boolean = true,
    isSmoothScroll: Boolean = true,
    onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) = this.bindToViewPager2(
    fragment.childFragmentManager,
    lifecycle,
    viewPager,
    items,
    customView,
    currentItemId,
    offscreenPageLimit,
    isEnableScrollPage,
    isSmoothScroll,
    onCreateFragment
)

// 绑定ViewPager2与TabLayout
fun <Data> TabLayout.bindToViewPager2(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    viewPager: ViewPager2,
    items: List<ViewPager2FragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPager2Item<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    isEnableScrollPage: Boolean = true,
    isSmoothScroll: Boolean = true,
    onCreateFragment: (Int, ViewPager2FragmentItem<Data>) -> Fragment
) {
    bindToViewPager2(
        lifecycle = lifecycle,
        viewPager = viewPager,
        adapter = ViewPager2FragmentAdapter(
            fragmentManager, lifecycle, items, onCreateFragment
        ),
        items = items,
        customView = customView,
        currentItemId = currentItemId,
        offscreenPageLimit = offscreenPageLimit,
        isEnableScrollPage = isEnableScrollPage,
        isSmoothScroll = isSmoothScroll
    )
}

// 绑定ViewPager2与TabLayout
fun <Data> TabLayout.bindToViewPager2(
    lifecycle: Lifecycle,
    viewPager: ViewPager2,
    adapter: RecyclerView.Adapter<*>,
    items: List<ViewPager2Item<Data>>,
    customView: ((TabLayout.Tab, ViewPager2Item<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT,
    isEnableScrollPage: Boolean = true,
    isSmoothScroll: Boolean = true
) {
    (viewPager.getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    viewPager.offscreenPageLimit = offscreenPageLimit
    viewPager.adapter = adapter
    viewPager.isUserInputEnabled = isEnableScrollPage
    val tabLayoutMediator =
        TabLayoutMediator(
            this,
            viewPager,
            true,
            isSmoothScroll
        ) { tab, position ->
            if (customView != null) customView.invoke(tab, items[position], position)
            else tab.text = items[position].title
        }
    tabLayoutMediator.attach()
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            lifecycle.removeObserver(this)
            viewPager.removeAllViews()
            tabLayoutMediator.detach()
        }
    })
    disabledTooltipText()
    val selectedId = currentItemId ?: items.firstOrNull { it.selected == true }?.id
    if (!selectedId.isNullOrBlank()) {
        val selectTab = items.indexOfFirst { it.id == selectedId }
        if (selectTab > 0) {
//            getTabAt(selectTab)?.select()
            viewPager.setCurrentItem(selectTab, false)
        }
    }
}

open class ViewPager2Item<Data>(
    val id: String,
    val title: String,
    val data: Data? = null,
    val selected: Boolean? = null,
) {
    constructor(title: String, data: Data? = null, selected: Boolean? = null) :
            this(title, title, data, selected)
}

open class ViewPager2FragmentItem<Data>(
    id: String,
    title: String,
    data: Data? = null,
    val args: Bundle? = null,
    selected: Boolean? = null,
) : ViewPager2Item<Data>(
    id, title, data, selected
) {
    constructor(
        title: String,
        data: Data? = null,
        args: Bundle? = null,
        selected: Boolean? = null,
    ) : this(title, title, data, args, selected)
}

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