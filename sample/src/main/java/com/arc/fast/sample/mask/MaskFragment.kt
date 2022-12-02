package com.arc.fast.sample.mask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentMaskBinding

class MaskFragment : BaseFragment<FragmentMaskBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMaskBinding = FragmentMaskBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}