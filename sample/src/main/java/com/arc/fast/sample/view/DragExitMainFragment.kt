package com.arc.fast.sample.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentDragExitMainBinding

class DragExitMainFragment : BaseFragment<FragmentDragExitMainBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDragExitMainBinding = FragmentDragExitMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btn1.setOnClickListener {
            findNavController().navigate(
                DragExitMainFragmentDirections.actionDragExitMainFragmentToDragExitFragment()
            )
        }
        binding.btn2.setOnClickListener {
            startActivity(Intent(requireContext(), DragExitActivity::class.java))
        }
        binding.btn3.setOnClickListener {
            startActivity(Intent(requireContext(), ImageDragExitActivity::class.java))
        }
    }
}