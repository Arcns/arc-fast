package com.arc.fast.sample.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.arc.fast.core.extensions.dp
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentDragExitBinding
import com.arc.fast.sample.databinding.FragmentDragExitMainBinding
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.arc.fast.sample.main.MainFragmentDirections
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DragExitMainFragment : BaseFragment<FragmentDragExitMainBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDragExitMainBinding = FragmentDragExitMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        binding.btn1.setOnClickListener {
            findNavController().navigate(
                DragExitMainFragmentDirections.actionDragExitMainFragmentToDragExitFragment()
            )
        }
        binding.btn2.setOnClickListener {
            startActivity(Intent(requireContext(), DragExitActivity::class.java))
        }
    }
}