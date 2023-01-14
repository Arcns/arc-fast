package com.arc.fast.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.arc.fast.sample.databinding.ActivityContainerBinding
import com.arc.fast.sample.common.extension.applyFullScreen
import com.arc.fast.sample.common.extension.setLightSystemBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 容器Activity
 */
class ContainerActivity : AppCompatActivity() {

    lateinit var binding: ActivityContainerBinding
    val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFullScreen()
        setLightSystemBar(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_container)
        // Toast
        appViewModel.eventToast.flowWithLifecycle(lifecycle).onEach {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }.launchIn(lifecycleScope)
    }

}