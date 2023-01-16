package com.arc.fast.sample.nsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arc.fast.core.extensions.ViewPager2FragmentItem
import com.arc.fast.core.extensions.bindToViewPager2
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentNestedScrollBinding

class NestedScrollCompatFragment : BaseFragment<FragmentNestedScrollBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentNestedScrollBinding =
        FragmentNestedScrollBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tl.bindToViewPager2(
            fragment = this,
            viewPager = binding.vp,
            items = listOf(
                ViewPager2FragmentItem<Unit>("ViewPager2+RecyclerView+Banner"),
                ViewPager2FragmentItem("ViewPager2+RecyclerView+HorizontalScrollView"),
                ViewPager2FragmentItem("ViewPager2+ViewPager2"),
            ),
            onCreateFragment = { position, _ ->
                when (position) {
                    0 -> NestedScrollCompatChildBannerFragment()
                    1 -> NestedScrollCompatChildHSBFragment()
                    else -> NestedScrollCompatChildViewPager2Fragment()
                }
            }
        )
    }
}