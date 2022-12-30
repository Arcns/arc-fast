package com.arc.fast.sample.test

import android.os.Bundle
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.arc.fast.core.extensions.ViewPager2FragmentItem
import com.arc.fast.core.extensions.bindToViewPager2
import com.arc.fast.core.extensions.dp
import com.arc.fast.sample.DATA
import com.arc.fast.sample.R
import com.arc.fast.sample.common.data.entity.TestListItem
import com.arc.fast.sample.common.data.entity.TestListItemType
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.arc.fast.sample.databinding.ItemTestPagerBinding
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TestAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    data: MutableList<TestListItem>
) :
    BaseProviderMultiAdapter<TestListItem>(data = data) {

    init {
        addItemProvider(TestTitleProvider())
        addItemProvider(TestPagerProvider(fragmentManager, lifecycle))
    }

    override fun getItemType(data: List<TestListItem>, position: Int): Int =
        data[position].type.value
}

class TestTitleProvider : BaseItemProvider<TestListItem>() {
    override val itemViewType: Int
        get() = TestListItemType.Title.value
    override val layoutId: Int
        get() = R.layout.item_test_card

    override fun onViewHolderCreated(
        viewHolder: BaseViewHolder,
        viewType: Int
    ) {
        ItemTestCardBinding.bind(viewHolder.itemView)
        super.onViewHolderCreated(viewHolder, viewType)
    }

    override fun convert(helper: BaseViewHolder, item: TestListItem) {
        DataBindingUtil.getBinding<ItemTestCardBinding>(helper.itemView)
            ?.apply {
                this.title = (item as TestListItem.TestTitle).title
                root.updateLayoutParams {
                    height = 56.dp
                }
                root.setOnClickListener { }
            }
    }
}

class TestPagerProvider(
    val fragmentManager: FragmentManager,
    val lifecycle: Lifecycle,
) : BaseItemProvider<TestListItem>() {
    override val itemViewType: Int
        get() = TestListItemType.Pager.value
    override val layoutId: Int
        get() = R.layout.item_test_pager

    override fun onViewHolderCreated(
        viewHolder: BaseViewHolder,
        viewType: Int
    ) {
        ItemTestPagerBinding.bind(viewHolder.itemView)
        super.onViewHolderCreated(viewHolder, viewType)
    }

    override fun convert(helper: BaseViewHolder, item: TestListItem) {
        DataBindingUtil.getBinding<ItemTestPagerBinding>(helper.itemView)
            ?.apply {
                tlTab.bindToViewPager2(
                    fragmentManager = fragmentManager,
                    lifecycle = lifecycle,
                    viewPager = vpPager,
                    (item as TestListItem.TestPager).data.map {
                        ViewPager2FragmentItem<Unit>(
                            id = it.tab,
                            title = it.tab,
                            args = Bundle().apply {
                                putParcelable(DATA, it)
                            })
                    }
                ) { _, _ ->
                    TestPagerItemFragment()
                }
            }
    }
}
