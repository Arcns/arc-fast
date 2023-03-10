package com.arc.fast.sample.test

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.arc.fast.core.extensions.dpToPx
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.common.data.entity.TestListItem
import com.arc.fast.sample.common.data.entity.TestListItemType
import com.arc.fast.sample.common.data.entity.TestPagerItem
import com.arc.fast.sample.databinding.FragmentTestBinding

class TestFragment : BaseFragment<FragmentTestBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentTestBinding = FragmentTestBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvData.itemAnimator?.changeDuration = 0
        val data = ArrayList<TestListItem>()
        for (i in 1..80) {
            data.add(TestListItem.TestTitle("test menu $i"))
        }
        val pagerData = ArrayList<TestPagerItem>()
        for (i in 1..10) {
            val items = ArrayList<String>()
            for (j in 1..20) {
                items.add(("pager $i data $j"))
            }
            pagerData.add(TestPagerItem("pager $i", items))
        }
        data.add(TestListItem.TestPager(pagerData))
        for (i in 1..20) {
            data.add(TestListItem.TestTitle("test menu $i"))
        }
        binding.rvData.addItemDecoration(object : ItemDecoration() {
            private val dp10 by lazy { 10.dpToPx }
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildLayoutPosition(view)
                val item = data.getOrNull(position) ?: return
                if (item.type == TestListItemType.Title) {
                    outRect.top = dp10
                    outRect.left = dp10
                    outRect.right = dp10
                } else if (item.type == TestListItemType.Pager) {
                    outRect.top = dp10
                }
            }
        })
        binding.rvData.adapter = TestAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            data = data
        )
    }

}