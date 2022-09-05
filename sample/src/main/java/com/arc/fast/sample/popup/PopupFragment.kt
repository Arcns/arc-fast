package com.arc.fast.sample.popup

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentPopupBinding

class PopupFragment : BaseFragment<FragmentPopupBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentPopupBinding = FragmentPopupBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        binding.btnBottomPopup.setOnClickListener {
            TestBottomPopupWindow(requireContext()).showAtLocation(
                binding.root,
                Gravity.BOTTOM,
                0,
                0
            )
        }
        binding.btnTopToAnchorBottomPopup.setOnClickListener {
            TestTopToAnchorBottomPopupWindow(requireContext()).showAsDropDown(binding.btnTopToAnchorBottomPopup)
        }
        binding.btnBottomToAnchorTopPopup.setOnClickListener {
            TestBottomToAnchorTopPopupWindow(requireContext()).showAsDropDown(
                binding.btnBottomToAnchorTopPopup,
                0,
                0,
                Gravity.BOTTOM
            )
        }
    }

}