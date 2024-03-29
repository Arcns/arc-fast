package com.arc.fast.collection.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.arc.fast.immersive.ImmersiveDialog

abstract class FastBindingDialog<Binding : ViewBinding> : ImmersiveDialog() {
    protected lateinit var binding: Binding
    override val layoutId: Int = -1
    abstract fun createBinding(inflater: LayoutInflater): Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater)
        return immersiveDialogConfigUtil.wrapDialogContentView(this, inflater, binding.root)
    }
}