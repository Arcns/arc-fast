package com.arc.fast.sample.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arc.fast.core.extensions.resToColor
import com.arc.fast.core.screenHeight
import com.arc.fast.immersive.ImmersivePopupWindow
import com.arc.fast.immersive.ImmersivePopupWindowConfig
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.DialogTestBottomBinding

class TestBottomPopupWindow(val context: Context) :
    ImmersivePopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, context.screenHeight / 2) {

    val binding: DialogTestBottomBinding by lazy {
        DialogTestBottomBinding.inflate(LayoutInflater.from(context))
    }

    override fun getImmersivePopupWindowConfig(context: Context) =
        ImmersivePopupWindowConfig.createBottomPopupWindow(context).apply {
//            navigationColor = Color.TRANSPARENT
            isLightNavigationBarForegroundColor = null
            backgroundColor = R.color.md_theme_light_primary_transparent_26.resToColor
        }

    init {
        animationStyle = com.arc.fast.immersive.R.style.StyleArcFastAnimDialogDownEnterExit
        contentView = binding.root
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}