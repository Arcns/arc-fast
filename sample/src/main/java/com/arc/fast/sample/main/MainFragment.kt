package com.arc.fast.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arc.fast.sample.*
import com.arc.fast.sample.common.data.entity.Menu
import com.arc.fast.sample.databinding.FragmentMainBinding
import com.arc.fast.sample.view.DragExitActivity
import com.arc.fast.sample.view.DragExitMainFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private var recyclerViewY = 0L

    override var isBackCLoseApp: Boolean = true

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
        viewModel = this@MainFragment.viewModel.apply {
            appViewModel = this@MainFragment.appViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 滑动时抬高appbar的层级
        binding.rvContent.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    recyclerViewY += dy
                    binding.appbar.isLifted = recyclerViewY != 0L
                }
            })
        viewModel.valueMenuList.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
            if (it.isNullOrEmpty()) viewModel.loadMenu()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        // 点击菜单回调
        viewModel.eventMenuClick.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
            onMenuClick(it)
        }.launchIn(lifecycleScope)
    }

    private fun onMenuClick(menu: Menu) {
        when (menu.url) {
            ACTION_INTRODUCTION -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("本项目包含一系列Android开发的便携工具，主要包括Fast Permission、Immersive Dialog、Immersive PopupWindow、Fast Span、Fast Mask等，能够让你快速、优雅的享受安卓便捷开发～")
                    .setPositiveButton("确定") { _, _ -> }
                    .show()
            }
            ACTION_CORE -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToCoreFragment()
                )
            }
            ACTION_PERMISSION -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToPermissionFragment()
                )
            }
            ACTION_DIALOG -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToDialogFragment()
                )
            }
            ACTION_POPUP -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToPopupFragment()
                )
            }
            ACTION_SPAN -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSpanFragment()
                )
            }
            ACTION_MASK -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToMaskFragment()
                )
            }
            ACTION_VIEW -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToViewFragment()
                )
            }
            ACTION_FAST_TEXT_VIEW -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToFastTextViewFragment()
                )
            }
            ACTION_NSC -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToNestedScrollCompatFragment()
                )
            }
            ACTION_DRAG_EXIT_LAYOUT -> {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToDragExitMainFragment()
                )
            }
            ACTION_TEST -> {
                findNavController().navigate(MainFragmentDirections.actionGlobalTestFragment())
            }
        }
    }

}