package com.arc.fast.sample.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arc.fast.core.extensions.color
import com.arc.fast.core.extensions.dp
import com.arc.fast.core.util.ImmersivePopupWindow
import com.arc.fast.core.util.ImmersivePopupWindowConfig
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.PopupTestTopBinding

class TestTopPopupWindow(val context: Context) :
    ImmersivePopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, 56.dp) {

    val binding: PopupTestTopBinding by lazy {
        PopupTestTopBinding.inflate(LayoutInflater.from(context))
    }

    override fun getImmersivePopupWindowConfig(context: Context) =
        ImmersivePopupWindowConfig.createTopPopupWindow(context).apply {
            backgroundColor = R.color.md_theme_light_primary_transparent_26.color
        }

    init {
        contentView = binding.root
        binding.tvMenu1.setOnClickListener {
            dismiss()
        }
        binding.tvMenu2.setOnClickListener {
            dismiss()
        }
        binding.tvMenu3.setOnClickListener {
            dismiss()
        }
    }
}