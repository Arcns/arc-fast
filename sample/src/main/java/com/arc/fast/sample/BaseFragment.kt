package com.arc.fast.sample

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<Binding : ViewDataBinding> :
    Fragment() {

    protected lateinit var binding: Binding
    protected val appViewModel: AppViewModel by activityViewModels()
    protected val navigationIconForBack: Drawable
        get() = DrawerArrowDrawable(requireContext()).apply {
            progress = 1f
        }
    protected open var isBackCLoseApp = false

    protected abstract fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = onCreateBinding(inflater, container, savedInstanceState)
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    protected open fun onActivityCreated() {
        findNavController().enableOnBackPressed(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                onActivityCreated()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isBackCLoseApp) {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        requireActivity().finishAffinity()
                    }
                })
        }
    }
}

