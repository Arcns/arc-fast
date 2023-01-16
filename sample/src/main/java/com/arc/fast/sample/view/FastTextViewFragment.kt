package com.arc.fast.sample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentFastTextViewBinding

class FastTextViewFragment : BaseFragment<FragmentFastTextViewBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentFastTextViewBinding = FragmentFastTextViewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}