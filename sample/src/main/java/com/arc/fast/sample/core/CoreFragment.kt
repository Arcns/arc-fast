package com.arc.fast.sample.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentCoreBinding

class CoreFragment : BaseFragment<FragmentCoreBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentCoreBinding = FragmentCoreBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}