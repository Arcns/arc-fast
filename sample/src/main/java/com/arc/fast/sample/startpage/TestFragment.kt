package com.arc.fast.sample.startpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentTestBinding
import com.arc.fast.sample.utils.NavTransitionOptions

class TestFragment : BaseFragment<FragmentTestBinding>() {
    override fun onCreateTransition(): NavTransitionOptions {
        return NavTransitionOptions(
            enter = null,
            exit = null,
            isSharedElementsDestination = true
        ).addSharedElements(R.id.tvAppName to "shared_element_app_name")
            .addSharedElements(R.id.tvVersion to "shared_element_version")
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentTestBinding = FragmentTestBinding.inflate(inflater, container, false)


}