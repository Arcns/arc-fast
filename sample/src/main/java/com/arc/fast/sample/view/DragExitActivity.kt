package com.arc.fast.sample.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arc.fast.sample.R
import com.arc.fast.sample.common.extension.applyFullScreen
import com.arc.fast.sample.common.extension.setLightSystemBar
import com.arc.fast.sample.databinding.FragmentDragExitBinding

/**
 */
class DragExitActivity : AppCompatActivity() {

    lateinit var binding: FragmentDragExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFullScreen()
        setLightSystemBar(true)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_drag_exit)
        binding.dragExitLayout.enableDragExit(bindExitActivity = this)
    }

}