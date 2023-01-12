package com.arc.fast.sample.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arc.fast.core.extensions.string
import com.arc.fast.sample.*
import com.arc.fast.sample.common.data.DataSource
import com.arc.fast.sample.common.data.entity.ApiResult
import com.arc.fast.sample.common.data.entity.Menu
import com.arc.fast.sample.common.data.entity.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
//        dataSource.menuList().onEach {
////            if (isFirstLoad && !it.isLoading) delay(1500)
////            valueMenuList.value = it
//            if (!it.isLoading) {
//                // 测试数据
//                val response = Response<List<Menu>?>(
//                    "success", null, null, null, null, arrayListOf(
//                        Menu("webview", "http://www.baidu.com", 0),
//                        Menu("scan", ACTION_SCAN, 1),
//                        Menu(R.string.immersive_dialog.string, ACTION_DIALOG, 1),
//                        Menu(R.string.immersive_popup.string, ACTION_POPUP, 1),
//                        Menu("test", ACTION_TEST, 0)
//                    )
//                )
//                valueMenuList.value = ApiResult.Success(response, response.data)
//            }
//        }.launchIn(viewModelScope)

        // 测试数据
        val response = Response<List<Menu>?>(
            "success", null, null, null, null,
            arrayListOf(
                Menu(
                    "一、介绍",
                    "Arc Fast介绍",
                    url = ACTION_PERMISSION
                ),
                Menu(
                    "二、Fast Core",
                    "Arc Fast Core",
                    url = ACTION_PERMISSION
                ),
                Menu(
                    "三、Fast Permission",
                    "一行代码实现基于Activity Result API的动态权限获取",
                    ACTION_PERMISSION
                ),
                Menu(
                    "四、Immersive Dialog",
                    "一行代码简单实现Android沉浸式Dialog",
                    ACTION_DIALOG
                ),
                Menu(
                    "五、Immersive PopupWindow",
                    "一行代码简单实现Android沉浸式PopupWindow",
                    ACTION_POPUP
                ),
                Menu(
                    "六、Fast Span",
                    "一行代码简单实现Android TextView常用样式Span",
                    ACTION_SPAN
                ),
                Menu(
                    "七、Fast Mask",
                    "一行代码简单实现Android遮罩镂空视图",
                    ACTION_MASK
                ),
                Menu(
                    "八、Fast View",
                    "一行代码简单实现Android常用View的圆角边框",
                    ACTION_VIEW
                ),
                Menu(
                    "九、Fast TextView",
                    "一行代码实现TextView中粗、四个方向drawable的不同Padding和宽高",
                    ACTION_FAST_TEXT_VIEW
                ),
                Menu(
                    "十、Fast NestedScrollCompat",
                    "一行代码解决Android滚动控件嵌套产生的滑动事件冲突",
                    ACTION_FAST_TEXT_VIEW
                ),
                Menu(
                    "十一、Fast DragExitLayout",
                    "一行代码实现Android仿小红书、Lemon8拖拽退出效果突",
                    ACTION_DRAG_EXIT_LAYOUT
                ),
                Menu(
                    "十一、Fast DragExitLayout",
                    "一行代码实现Android仿小红书、Lemon8拖拽退出效果突",
                    ACTION_DRAG_EXIT_LAYOUT_ACTIVITY,
                    1
                ),
//                Menu("test", ACTION_TEST, 0),
//                Menu("test_webview", "http://www.baidu.com", 0),
//                Menu("test_scan", ACTION_SCAN, 0),
            )
        )
        valueMenuList.value = ApiResult.Success(response, response.data)
    }

    fun onMenuClick(item: Menu) {
        viewModelScope.launch {
            eventMenuClick.emit(item)
        }
    }
}