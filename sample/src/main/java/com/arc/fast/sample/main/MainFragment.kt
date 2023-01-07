package com.arc.fast.sample.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arc.fast.core.extensions.isInternetResources
import com.arc.fast.sample.*
import com.arc.fast.sample.common.data.LocalData
import com.arc.fast.sample.common.data.entity.Menu
import com.arc.fast.sample.databinding.FragmentMainBinding
import com.arc.fast.sample.common.extension.titleTextView
import com.arc.fast.sample.common.utils.NavTransitionOptions
import com.arc.fast.sample.common.utils.SHARED_ELEMENT_APP_NAME
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel: MainViewModel by viewModels()
    private var recyclerViewY = 0L

    override fun onCreateTransition(): NavTransitionOptions {
        return NavTransitionOptions(
            isSharedElementsDestination = true
        ).addSharedElementViews({ root: View ->
            binding.toolbar.titleTextView
        } to SHARED_ELEMENT_APP_NAME)
    }

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
        // 注销
        binding.ivLogout.setOnClickListener {
            LocalData.currentLoginSecret = null
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToLoginFragment(),
                FragmentNavigatorExtras(
                    binding.toolbar.titleTextView!! to SHARED_ELEMENT_APP_NAME
                )
            )
        }
        // 下拉刷新
        binding.refreshLayout.setOnRefreshListener {
            viewModel.loadMenu()
        }
        // 返回时的元素转场动画（列表加载后再执行动画）
        postponeEnterTransition()
        binding.rvContent.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        lifecycleScope.launch {
            delay(500)
            // 监听列表加载清空来控制下拉刷新
            viewModel.valueMenuList.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
                if (it == null) {
                    // 首次监听触发null回调
                    binding.refreshLayout.isRefreshing = true
                    viewModel.loadMenu(true)
                } else if (!it.isLoading) {
                    // 加载完成后结束下拉刷新
                    binding.refreshLayout.isRefreshing = false
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        // 点击菜单回调
        viewModel.eventMenuClick.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
            onMenuClick(it)
        }.launchIn(lifecycleScope)
    }

    private fun onMenuClick(menu: Menu) {
        if (menu.url?.isInternetResources == true) {
            val sharedElementView = if (menu.isFullScreen) null else
                viewModel.valueMenuList.value?.response?.data?.indexOf(menu)?.let { itemIndex ->
                    if (itemIndex >= 0) binding.rvContent.findViewHolderForAdapterPosition(
                        itemIndex
                    ) else null
                }?.itemView?.findViewById<View>(R.id.tvTitle)
            if (sharedElementView != null && !menu.title.isNullOrBlank()) {
                // 设置元素转场动画
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToWebViewFragment(
                        menu
                    ),
                    FragmentNavigatorExtras(
                        sharedElementView to menu.title!!
                    )
                )
            } else {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToWebViewFragment(
                        menu
                    )
                )
            }
        } else if (menu.url == ACTION_SCAN) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToScanFragment()
            )
        } else if (menu.url == ACTION_PERMISSION) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToPermissionFragment()
            )
        } else if (menu.url == ACTION_DIALOG) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToDialogFragment()
            )
        } else if (menu.url == ACTION_POPUP) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToPopupFragment()
            )
        } else if (menu.url == ACTION_SPAN) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToSpanFragment()
            )
        } else if (menu.url == ACTION_MASK) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToMaskFragment()
            )
        } else if (menu.url == ACTION_VIEW) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToViewFragment()
            )
        } else if (menu.url == ACTION_FAST_TEXT_VIEW) {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToFastTextViewFragment()
            )
        } else if (menu.url == ACTION_TEST) {
            findNavController().navigate(MainFragmentDirections.actionGlobalTestFragment())
        }
    }

}