package com.arc.fast.sample.startpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.common.data.LocalData
import com.arc.fast.sample.databinding.FragmentStartPageBinding
import com.arc.fast.sample.common.utils.NavTransitionOptions
import com.arc.fast.sample.common.utils.SHARED_ELEMENT_APP_NAME
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartPageFragment : BaseFragment<FragmentStartPageBinding>() {
    override fun onCreateTransition(): NavTransitionOptions {
        return NavTransitionOptions(
            isSharedElementsDestination = true
        ).addSharedElements(R.id.tvAppName to SHARED_ELEMENT_APP_NAME)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentStartPageBinding = FragmentStartPageBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(500)
            appViewModel.appUpdate()
            LocalData.currentLoginSecret = "test" // 测试数据
            if (LocalData.currentLoginSecret.isNullOrBlank()) {
                // 未登陆
                findNavController().navigate(
                    StartPageFragmentDirections.actionStartPageFragmentToLoginFragment(),
                    FragmentNavigatorExtras(
                        binding.tvAppName to SHARED_ELEMENT_APP_NAME
                    )
                )
            } else {
                // 已登陆
                findNavController().navigate(
                    StartPageFragmentDirections.actionStartPageFragmentToMainFragment(),
                    FragmentNavigatorExtras(
                        binding.tvAppName to SHARED_ELEMENT_APP_NAME
                    )
                )
            }
//            findNavController().navigate(
//                StartPageFragmentDirections.actionStartPageFragmentToTestFragment(),
//                FragmentNavigatorExtras(
//                    binding.tvAppName to "shared_element_app_name"
//                )
//            )
        }
    }

}