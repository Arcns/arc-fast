package com.arc.fast.sample

import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {


    // 事件
    val eventToast = MutableSharedFlow<String?>()
    val eventScanResult =
        MutableSharedFlow<String>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )


    fun showToast(message: String?) {
        viewModelScope.launch {
            eventToast.emit(message)
        }
    }

    fun setScanResult(value: String) {
        viewModelScope.launch {
            delay(500)
            eventScanResult.emit(value)
        }
    }
}