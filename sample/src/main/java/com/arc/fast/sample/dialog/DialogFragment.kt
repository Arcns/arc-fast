package com.arc.fast.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentDialogBinding

class DialogFragment : BaseFragment<FragmentDialogBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDialogBinding = FragmentDialogBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCenterDialog.setOnClickListener {
            TestCenterDialog().show(parentFragmentManager, null)
        }
        binding.btnBottomDialog.setOnClickListener {
            TestBottomDialog().show(parentFragmentManager, null)
        }
        binding.btnInputDialog.setOnClickListener {
            TestInputDialog().show(parentFragmentManager, null)
        }
    }

}