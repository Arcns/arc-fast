package com.arc.fast.sample.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arc.fast.sample.*
import com.arc.fast.sample.common.data.entity.Menu
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // 全局viewModel
    var appViewModel: AppViewModel? = null

    // 菜单
    val valueMenuList = MutableStateFlow<List<Menu>?>(null)

    // 菜单跳转
    val eventMenuClick = MutableSharedFlow<Menu>()

    fun loadMenu() {
        // 测试数据
        valueMenuList.value = arrayListOf(
            Menu(
                "一、介绍",
                "Arc Fast介绍",
                url = ACTION_INTRODUCTION
            ),
            Menu(
                "二、Fast Resource",
                "一行代码简单实现Android dp2px、sp2px、常用Resource值(string/color/drawable)获取",
                ACTION_CORE
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
                ACTION_NSC
            ),
            Menu(
                "十一、Fast DragExitLayout",
                "一行代码实现Android仿小红书、Lemon8拖拽退出效果",
                ACTION_DRAG_EXIT_LAYOUT, 1
            ),
            Menu(
                "其他测试",
                "测试页面",
                ACTION_TEST,
                1
            ),
        )
    }

    fun onMenuClick(item: Menu) {
        viewModelScope.launch {
            eventMenuClick.emit(item)
        }
    }
}