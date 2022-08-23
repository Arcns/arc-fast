package com.arc.fast.sample.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arc.fast.sample.AppViewModel
import com.arc.fast.sample.data.DataSource
import com.arc.fast.sample.data.entity.ApiResult
import com.arc.fast.sample.data.entity.Menu
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // 全局viewModel
    var appViewModel: AppViewModel? = null

    // 数据源
    private val dataSource by lazy { DataSource() }

    // 菜单
    val valueMenuList = MutableStateFlow<ApiResult<List<Menu>?>?>(null)

    // 菜单跳转
    val eventMenuClick = MutableSharedFlow<Menu>()

    fun loadMenu(isFirstLoad: Boolean = false) {
        dataSource.menuList().onEach {
//            if (isFirstLoad && !it.isLoading) delay(1500)
            valueMenuList.value = it
        }.launchIn(viewModelScope)
    }

    fun onMenuClick(item: Menu) {
        viewModelScope.launch {
            eventMenuClick.emit(item)
        }
    }
}