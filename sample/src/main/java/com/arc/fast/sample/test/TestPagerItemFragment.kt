package com.arc.fast.sample.test

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.arc.fast.core.extensions.dp
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.DATA
import com.arc.fast.sample.R
import com.arc.fast.sample.data.entity.TestPagerItem
import com.arc.fast.sample.databinding.FragmentTestPagerItemBinding
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TestPagerItemFragment :
    BaseFragment<FragmentTestPagerItemBinding>() {

    private val data: TestPagerItem by lazy { arguments?.getParcelable(DATA)!! }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentTestPagerItemBinding =
        FragmentTestPagerItemBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvData.itemAnimator?.changeDuration = 0
        binding.rvData.addItemDecoration(object : ItemDecoration() {
            private val dp10 by lazy { 10.dp }
            private val dp5 by lazy { 5.dp }
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildLayoutPosition(view)
                if (position % 2 == 0) {
                    outRect.top = dp10
                    outRect.left = dp10
                    outRect.right = dp5
                } else {
                    outRect.top = dp10
                    outRect.left = dp5
                    outRect.right = dp10
                }
            }
        })
        binding.rvData.adapter =
            object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_test_card, data.data) {
                val heights by lazy { arrayOf(200.dp, 250.dp, 300.dp) }

                override fun createBaseViewHolder(view: View): BaseViewHolder {
                    ItemTestCardBinding.bind(view)
                    return super.createBaseViewHolder(view)
                }

                override fun convert(holder: BaseViewHolder, item: String) {
                    DataBindingUtil.getBinding<ItemTestCardBinding>(holder.itemView)?.apply {
                        root.updateLayoutParams {
                            height = heights.take(1).first()
                        }
                        title = item
                    }
                }
            }
    }

}