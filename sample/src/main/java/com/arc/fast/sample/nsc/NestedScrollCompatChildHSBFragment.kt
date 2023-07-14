package com.arc.fast.sample.nsc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentNestedScrollChildBannerBinding
import com.arc.fast.sample.databinding.FragmentNestedScrollChildHsbBinding
import com.arc.fast.sample.databinding.ItemNscChildBannerBinding
import com.arc.fast.sample.databinding.ItemNscChildBannerImageBinding
import com.arc.fast.sample.databinding.ItemNscChildHsbBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator

class NestedScrollCompatChildHSBFragment : BaseFragment<FragmentNestedScrollChildHsbBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentNestedScrollChildHsbBinding =
        FragmentNestedScrollChildHsbBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = ArrayList<Int>()
        for (i in 0..50) {
            data.add(i)
        }
        binding.rv.adapter = object : BaseQuickAdapter<Int, BaseViewHolder>(
            R.layout.item_nsc_child_hsb, data
        ) {
            override fun convert(holder: BaseViewHolder, item: Int) {
            }
        }
    }
}