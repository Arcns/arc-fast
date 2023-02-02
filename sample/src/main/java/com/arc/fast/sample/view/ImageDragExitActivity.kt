package com.arc.fast.sample.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arc.fast.core.screenHeight
import com.arc.fast.sample.R
import com.arc.fast.sample.common.extension.applyFullScreen
import com.arc.fast.sample.common.extension.setLightSystemBar
import com.arc.fast.sample.databinding.ActivityImageDragExitBinding
import com.arc.fast.view.FastDragExitLayout
import kotlin.math.abs

/**
 */
class ImageDragExitActivity : AppCompatActivity() {

    lateinit var binding: ActivityImageDragExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFullScreen()
        setLightSystemBar(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_drag_exit)
        // 启用拖拽退出
        binding.dragExitLayout.enableDragExit(
            bindExitActivity = this,
            onDragCallback = object : FastDragExitLayout.OnDragCallback {
                override fun onDrag(left: Int, top: Int, scale: Float) {
                    binding.vBg.alpha = 1 - abs(top) * 1.5f / screenHeight
                }

                override fun onEnd(isExit: Boolean) {
                    if (!isExit) binding.vBg.alpha = 1f
                }
            })
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

}