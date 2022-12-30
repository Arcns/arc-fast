package com.arc.fast.sample.test.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arc.fast.core.extensions.string
import com.arc.fast.sample.AppViewModel
import com.arc.fast.sample.SampleApp
import com.arc.fast.sample.R
import com.arc.fast.sample.common.data.ApiStatus
import com.arc.fast.sample.common.data.DataSource
import com.arc.fast.sample.common.data.LocalData
import com.arc.fast.sample.common.data.entity.ApiResult
import com.arc.fast.sample.common.data.entity.Dialog
import kotlinx.coroutines.flow.*

class LoginViewModel : ViewModel() {
    // 全局viewModel
    var appViewModel: AppViewModel? = null

    // 数据源
    private val dataSource by lazy { DataSource() }

    // 是否为debug
    val valueIsDebug = MutableStateFlow(SampleApp.isDebug)

    // 当前环境名称
    val valueEnvironment = MutableStateFlow(LocalData.currentEnvironmentName)

    // 输入框的值
    val valueAccount = MutableStateFlow("")
    val valuePassword = MutableStateFlow("")
    val valueVerification = MutableStateFlow("")

    // 验证码图片
    val valueVerificationImage = MutableStateFlow("")

    // 是否启用验证码
    val valueEnableVerification = MutableStateFlow(false)

    // 是否启用登陆按钮
    val valueEnableLogin =
        combine(
            valueAccount,
            valuePassword,
            valueVerification,
            valueEnableVerification
        ) { account, password, verification, enableVerification ->
            account.isNotBlank() && password.isNotBlank() && (!enableVerification || verification.isNotBlank())
        }.asLiveData(viewModelScope.coroutineContext)

    // 登陆结果
    val eventLoginResult = MutableSharedFlow<ApiResult<Any?>>()
    val valueLoginResult = eventLoginResult.asLiveData(viewModelScope.coroutineContext)

    // 点击登陆
    fun onLogin() {
        dataSource.login(
            valueAccount.value,
            valuePassword.value,
            valueLoginResult.value?.response?.sessionid,
            if (valueEnableVerification.value) valueVerification.value else null
        ).onEach {
            eventLoginResult.emit(it)
            when (it.status) {
                ApiStatus.SUCCESS -> {
                    appViewModel?.showToast(it.message)
                    LocalData.currentLoginSecret = it.response?.secret
                }
                ApiStatus.ERROR -> {
                    if (it.response?.errorCode?.let { it > 1 } == true) {
                        valueEnableVerification.value = true
                        onUpdateVerificationImage()
                    }
                    appViewModel?.showDialog(
                        Dialog(
                            title = R.string.error.string,
                            message = it.message
                        )
                    )
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 更新验证码图片
    fun onUpdateVerificationImage() {
        valueVerificationImage.value =
            dataSource.verificationImag(valueLoginResult.value?.response?.sessionid)
    }

    // 切换环境
    fun onSwitchEnvironment(environment: String) {
        LocalData.currentEnvironmentValue = environment
        valueEnvironment.value = LocalData.currentEnvironmentName
    }
}