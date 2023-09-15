package com.arc.fast.core.extensions


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

// 绑定ViewPager与TabLayout
fun <Data> TabLayout.bindToViewPager(
    activity: AppCompatActivity,
    viewPager: ViewPager,
    items: List<ViewPagerFragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPagerItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = 1,
    isEnableScrollPage: Boolean = true,
    onCreateFragment: (Int, ViewPagerFragmentItem<Data>) -> Fragment
) = this.bindToViewPager(
    activity.supportFragmentManager,
    viewPager,
    items,
    customView,
    currentItemId,
    offscreenPageLimit,
    isEnableScrollPage,
    onCreateFragment
)

// 绑定ViewPager与TabLayout
fun <Data> TabLayout.bindToViewPager(
    fragment: Fragment,
    viewPager: ViewPager,
    items: List<ViewPagerFragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPagerItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = 1,
    isEnableScrollPage: Boolean = true,
    onCreateFragment: (Int, ViewPagerFragmentItem<Data>) -> Fragment
) = this.bindToViewPager(
    fragment.childFragmentManager,
    viewPager,
    items,
    customView,
    currentItemId,
    offscreenPageLimit,
    isEnableScrollPage,
    onCreateFragment
)

// 绑定ViewPager与TabLayout
fun <Data> TabLayout.bindToViewPager(
    fragmentManager: FragmentManager,
    viewPager: ViewPager,
    items: List<ViewPagerFragmentItem<Data>>,
    customView: ((TabLayout.Tab, ViewPagerItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    offscreenPageLimit: Int = 1,
    isEnableScrollPage: Boolean = true,
    onCreateFragment: (Int, ViewPagerFragmentItem<Data>) -> Fragment
) {
    bindToViewPager(
        viewPager = viewPager,
        adapter = ViewPagerFragmentAdapter(
            fragmentManager, items, onCreateFragment
        ),
        items = items,
        customView = customView,
        currentItemId = currentItemId,
        isEnableScrollPage = isEnableScrollPage,
        offscreenPageLimit = offscreenPageLimit,
    )
}

// 绑定ViewPager与TabLayout
fun <Data> TabLayout.bindToViewPager(
    viewPager: ViewPager,
    adapter: FragmentStatePagerAdapter,
    items: List<ViewPagerItem<Data>>,
    customView: ((TabLayout.Tab, ViewPagerItem<Data>, Int) -> Unit)? = null,
    currentItemId: String? = null,
    isEnableScrollPage: Boolean = true,
    offscreenPageLimit: Int = 1,
) {
    if (viewPager is FastViewPager) viewPager.scrollable = isEnableScrollPage
    viewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    viewPager.offscreenPageLimit = offscreenPageLimit
    viewPager.adapter = adapter
    setupWithViewPager(viewPager)
    disabledTooltipText()
    if (customView != null) {
        forEachIndexed { i, tab ->
            customView(tab, items[i], i)
        }
    }
    val selectedId = currentItemId ?: items.firstOrNull { it.selected == true }?.id
    var selectTab = 0
    if (!selectedId.isNullOrBlank()) {
        selectTab = items.indexOfFirst { it.id == selectedId }
        if (selectTab > 0) {
//            getTabAt(selectTab)?.select()
            viewPager.setCurrentItem(selectTab, false)
        }
    }
    selectTab(getTabAt(selectTab))
}

open class ViewPagerItem<Data>(
    val id: String,
    val title: String,
    val data: Data? = null,
    val selected: Boolean? = null,
) {
    constructor(title: String, data: Data? = null, selected: Boolean? = null) :
            this(title, title, data, selected)
}

open class ViewPagerFragmentItem<Data>(
    id: String,
    title: String,
    data: Data? = null,
    val args: Bundle? = null,
    selected: Boolean? = null,
) : ViewPagerItem<Data>(
    id, title, data, selected
) {
    constructor(
        title: String,
        data: Data? = null,
        args: Bundle? = null,
        selected: Boolean? = null,
    ) : this(title, title, data, args, selected)
}

class ViewPagerFragmentAdapter<Data>(
    fragmentManager: FragmentManager,
    private val items: List<ViewPagerFragmentItem<Data>>,
    private val onCreateFragment: (Int, ViewPagerFragmentItem<Data>) -> Fragment
) : FragmentStatePagerAdapter(fragmentManager) {
    var currentFragment: Fragment? = null
        private set

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is Fragment) {
            currentFragment = `object`
        }
        super.setPrimaryItem(container!!, position, `object`)
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Fragment {
        return onCreateFragment(position, items[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }

    override fun saveState(): Parcelable? {
        return null
    }

}

// 支持控制是否可滑动的ViewPager
open class FastViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewPager(context, attrs) {

    var scrollable = true

    override fun canScrollHorizontally(direction: Int): Boolean {
        return scrollable && super.canScrollHorizontally(direction)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return scrollable && super.canScrollVertically(direction)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (!scrollable) {
            false
        } else {
            try {
                super.onTouchEvent(ev)
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (!scrollable) {
            false
        } else {
            try {
                super.onInterceptTouchEvent(ev)
            } catch (e: Throwable) {
                e.printStackTrace()
                false
            }
        }
    }

}