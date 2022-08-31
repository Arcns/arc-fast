package com.arc.fast.sample.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.WindowInsetsAnimationCompat
import com.arc.fast.core.extensions.applyWindowInsetIMEAnimation
import com.arc.fast.core.extensions.color
import com.arc.fast.core.extensions.showSoftInput
import com.arc.fast.core.util.ImmersiveDialogConfig
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.DialogTestInputBinding

class TestInputDialog : ImmersiveBindingDialog<DialogTestInputBinding>() {

    override fun createBinding(inflater: LayoutInflater): DialogTestInputBinding =
        DialogTestInputBinding.inflate(inflater)

    override val immersiveDialogConfig
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        // R版本及以上可直接使用BottomDialogConfig实现完美的IME跟踪效果
            ImmersiveDialogConfig.createBottomDialogConfig().apply {
                backgroundDimEnabled = false
                backgroundColor = R.color.md_theme_light_primary_transparent_26.color
                animations = 0
                enableWrapDialogContentView = false
            }
        else
            ImmersiveDialogConfig.createSoftInputAdjustResizeDialogConfig().apply {
                backgroundDimEnabled = false
                backgroundColor = R.color.md_theme_light_primary_transparent_26.color
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