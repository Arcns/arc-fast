package com.arc.fast.sample.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.data.ApiStatus
import com.arc.fast.sample.data.LocalData
import com.arc.fast.sample.databinding.FragmentLoginBinding
import com.arc.fast.sample.utils.NavTransitionOptions
import com.arc.fast.sample.utils.SHARED_ELEMENT_APP_NAME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateTransition(): NavTransitionOptions {
        return NavTransitionOptions(
            isSharedElementsDestination = true
        ).addSharedElements(R.id.tvAppName to SHARED_ELEMENT_APP_NAME)
    }

    override var isBackCLoseApp: Boolean = true

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false).apply {
        viewModel = this@LoginFragment.viewModel.apply {
            this.appViewModel = this@LoginFragment.appViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSwitchEnvironment.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.switch_environment_title)
                .setSingleChoiceItems(
                    LocalData.environments.map { getString(it.key) }.toTypedArray(),
                    LocalData.environments.values.indexOf(LocalData.currentEnvironmentValue)
                ) { dialog, which ->
                    viewModel.onSwitchEnvironment(LocalData.environments.values.toTypedArray()[which])
                    dialog.dismiss()
                }.setNegativeButton(R.string.cancel) { _, _ -> }.show()
        }
        viewModel.eventLoginResult.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach {
            // 测试用，任何清空均可以进入主页
            if (it.status == ApiStatus.SUCCESS || it.status == ApiStatus.ERROR) {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainFragment(),
                    FragmentNavigatorExtras(
                        binding.tvAppName to SHARED_ELEMENT_APP_NAME
                    )
                )
            }
        }.launchIn(lifecycleScope)
    }

}