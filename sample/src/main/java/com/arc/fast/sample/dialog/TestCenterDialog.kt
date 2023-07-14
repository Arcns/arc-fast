package com.arc.fast.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.arc.fast.core.extensions.dpToPx
import com.arc.fast.core.extensions.resToColor
import com.arc.fast.immersive.ImmersiveDialogConfig
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.DialogTestCenterBinding

class TestCenterDialog : ImmersiveBindingDialog<DialogTestCenterBinding>() {

    override fun createBinding(inflater: LayoutInflater): DialogTestCenterBinding =
        DialogTestCenterBinding.inflate(inflater)

    override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createFullScreenDialogConfig().apply {
            height = 300.dpToPx
            width = 300.dpToPx
            backgroundColor = R.color.md_theme_light_primary_transparent_26.resToColor
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}