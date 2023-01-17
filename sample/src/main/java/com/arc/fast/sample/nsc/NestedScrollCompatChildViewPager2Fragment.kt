package com.arc.fast.sample.nsc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arc.fast.core.extensions.ViewPager2FragmentItem
import com.arc.fast.core.extensions.bindToViewPager2
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentNestedScrollChildViewpager2Binding

class NestedScrollCompatChildViewPager2Fragment :
    BaseFragment<FragmentNestedScrollChildViewpager2Binding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentNestedScrollChildViewpager2Binding =
        FragmentNestedScrollChildViewpager2Binding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tl.bindToViewPager2(
            fragment = this,
            viewPager = binding.vp,
            items = listOf(
                ViewPager2FragmentItem<Unit>("ViewPager2+RecyclerView\n+Banner"),
                ViewPager2FragmentItem("ViewPager2+RecyclerView\n+HorizontalScrollView"),
            ),
            onCreateFragment = { position, _ ->
                when (position) {
                    0 -> NestedScrollCompatChildBannerFragment()
                    else -> NestedScrollCompatChildHSBFragment()
                }
            }
        )
    }
}