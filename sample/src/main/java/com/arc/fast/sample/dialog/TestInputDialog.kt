package com.arc.fast.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.WindowInsetsAnimationCompat
import com.arc.fast.core.extensions.resToColor
import com.arc.fast.core.extensions.showSoftInput
import com.arc.fast.immersive.ImmersiveDialogConfig
import com.arc.fast.immersive.applyWindowInsetIMEAnimation
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.DialogTestInputBinding

class TestInputDialog : ImmersiveBindingDialog<DialogTestInputBinding>() {

    override fun createBinding(inflater: LayoutInflater): DialogTestInputBinding =
        DialogTestInputBinding.inflate(inflater)

    override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createSoftInputAdjustResizeDialogConfig().apply {
            // 除非禁用backgroundDimEnabled否则navigationColor无效
            backgroundDimEnabled = false
            backgroundColor = R.color.md_theme_light_primary_transparent_26.resToColor
            animations = 0
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.clContent.applyWindowInsetIMEAnimation(
            dispatchMode = WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE,
            rootView = view
        )
        binding.etValue.post {
            binding.etValue.showSoftInput()
        }
    }
}