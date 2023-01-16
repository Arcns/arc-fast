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
import com.arc.fast.sample.databinding.ItemNscChildBannerBinding
import com.arc.fast.sample.databinding.ItemNscChildBannerImageBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator

class NestedScrollCompatChildBannerFragment : BaseFragment<FragmentNestedScrollChildBannerBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentNestedScrollChildBannerBinding =
        FragmentNestedScrollChildBannerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = ArrayList<Int>()
        for (i in 0..50) {
            data.add(i)
        }
        binding.rv.adapter = object : BaseQuickAdapter<Int, BaseViewHolder>(
            R.layout.item_nsc_child_banner, data
        ) {
            override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
                super.onItemViewHolderCreated(viewHolder, viewType)
                ItemNscChildBannerBinding.bind(viewHolder.itemView)
            }

            override fun convert(holder: BaseViewHolder, item: Int) {
                DataBindingUtil.getBinding<ItemNscChildBannerBinding>(holder.itemView)?.apply {
                    if (banner.adapter == null) {
                        banner.indicator = CircleIndicator(context)
                        banner.setAdapter(BannerAdapter(context))
                    }
                }
            }
        }
    }
}

private class BannerAdapter(
    val context: Context
) : BannerAdapter<Int, BannerViewHolder>(
    arrayListOf(
        R.mipmap.s1,
        R.mipmap.s2,
        R.mipmap.s3,
        R.mipmap.s4
    )
) {
    override fun onCreateHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerViewHolder = BannerViewHolder(
        ItemNscChildBannerImageBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
    )

    override fun onBindView(
        holder: BannerViewHolder,
        data: Int,
        position: Int,
        size: Int
    ) {
        holder.binding.ivImage.setImageResource(data)
    }

}

private class BannerViewHolder(val binding: ItemNscChildBannerImageBinding) :
    ViewHolder(binding.root)