package com.arc.fast.sample

import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arc.fast.sample.common.data.DataSource
import com.arc.fast.sample.common.data.entity.AppUpdate
import com.arc.fast.sample.common.data.entity.Dialog
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    // 数据源
    private val dataSource by lazy { DataSource() }

    // 事件
    val eventToast = MutableSharedFlow<String?>()
    val eventDialog = MutableSharedFlow<Dialog>()
    val eventAppUpdate = MutableSharedFlow<AppUpdate>()
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

    fun showDialog(dialog: Dialog) {
        viewModelScope.launch {
            eventDialog.emit(dialog)
        }
    }

    fun setScanResult(value: String) {
        viewModelScope.launch {
            delay(500)
            eventScanResult.emit(value)
        }
    }

    fun appUpdate() {
        dataSource.appUpdate().onEach {
            if (it.data?.apk_url.isNullOrBlank()) return@onEach
            if ((it.data?.version_code ?: 0L)
                <= PackageInfoCompat.getLongVersionCode(SampleApp.packageInfo)
            ) return@onEach
            eventAppUpdate.emit(it.data ?: return@onEach)
        }.launchIn(viewModelScope)
    }
}