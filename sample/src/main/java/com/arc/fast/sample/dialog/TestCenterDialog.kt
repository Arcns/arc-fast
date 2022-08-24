package com.arc.fast.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.arc.fast.core.extensions.color
import com.arc.fast.core.extensions.dp
import com.arc.fast.core.util.ImmersiveDialogConfig
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.DialogTestCenterBinding

class TestCenterDialog : ImmersiveBindingDialog<DialogTestCenterBinding>() {

    override fun createBinding(inflater: LayoutInflater): DialogTestCenterBinding =
        DialogTestCenterBinding.inflate(inflater)

    override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createFullScreenDialogConfig().apply {
            height = 300.dp
            width = 300.dp
            backgroundColor = R.color.md_theme_light_primary_transparent_26.color
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}