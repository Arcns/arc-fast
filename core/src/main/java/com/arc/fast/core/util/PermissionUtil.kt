package com.arc.fast.core.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.arc.fast.core.R
import com.arc.fast.core.extensions.string
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 权限工具
 * 注意：根据https://developer.android.com/training/basics/intents/result?hl=zh-cn
 * 使用前您必须在CREATED之前创建该工具
 */
class PermissionUtil {

    // 当前activity
    private var fragment: Fragment? = null
    private var activity: FragmentActivity? = null
        get() {
            if (field == null && fragment != null) {
                field = fragment?.activity
            }
            return field
        }

    // 当前默认生命周期
    private var defaultLifecycle: Lifecycle? = null

    // 权限启动器
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    // 权限请求结果自定义回调
    private var onLauncherResultCallback: ((result: Map<String, Boolean>) -> Unit)? = null

    // 是否已经准备好
    val isReady: Boolean get() = launcher != null

    // 当前请求已显示过理由了(避免再拒绝后重复显示)
    private var currentRequestHasShownRationalePermissions: List<String>? = null

    constructor(activity: FragmentActivity) {
        this.activity = activity
        this.defaultLifecycle = activity.lifecycle
        launcher = activity.registerForActivityResult(
            RequestMultiplePermissions()
        ) { result ->
            onLauncherResultCallback?.invoke(result)
            onLauncherResultCallback = null
        }
        // 设置生命周期管理
        registerReleaseByLifecycle(defaultLifecycle!!)
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        this.defaultLifecycle = fragment.lifecycle
        launcher = fragment.registerForActivityResult(
            RequestMultiplePermissions()
        ) { result ->
            onLauncherResultCallback?.invoke(result)
            onLauncherResultCallback = null
        }
        // 设置生命周期管理
        registerReleaseByLifecycle(defaultLifecycle!!)
    }

    /**
     * 请求权限
     */
    fun request(
        permission: String, //Manifest.permission.xxx
        @StringRes rationaleRes: Int? = null,
        onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
    ) {
        request(
            PermissionRequest(permission, rationaleRes),
            onResult = onResult
        )
    }

    /**
     * 请求权限
     */
    fun request(
        vararg permissions: String, //Manifest.permission.xxx
        @StringRes overallRationaleRes: Int? = null,
        onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
    ) {
        request(
            lifecycle = null,
            permissionRequests = permissions.map {
                PermissionRequest(permission = it)
            }.toTypedArray(),
            overallRationaleRes = overallRationaleRes,
            onResult = onResult
        )
    }

    /**
     * 请求权限
     */
    fun request(
        vararg permissionRequests: PermissionRequest,
        @StringRes overallRationaleRes: Int? = null,
        onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit
    ) {
        request(
            lifecycle = null,
            permissionRequests = permissionRequests,
            overallRationaleRes = overallRationaleRes,
            onResult = onResult
        )
    }

    /**
     * 请求权限
     */
    fun request(
        lifecycle: Lifecycle? = null,
        vararg permissionRequests: PermissionRequest,
        @StringRes overallRationaleRes: Int? = null,
        onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit
    ) {
        if (launcher == null) return
        val requireActivity = activity ?: return
        // 需要请求的权限
        val deniedPermissions = ArrayList<String>()
        // 权限结果
        val permissionResult = HashMap<String, PermissionResult>()
        // 权限提示
        val permissionRationales = HashMap<String, Int>()
        // 先查询当前的权限结果
        var beforeRationale: String? = null
        var beforeRationalePermissions = ArrayList<String>()
        permissionRequests.forEach {
            val permission = it.permission
            if (it.rationaleRes != null) {
                permissionRationales[permission] = it.rationaleRes
            }
            if (ContextCompat.checkSelfPermission(
                    requireActivity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 已有权限
                permissionResult[permission] = PermissionResult.Granted
            } else {
                // 无权限
                permissionResult[permission] = PermissionResult.Denied
                deniedPermissions.add(permission)
                // 是否需要先显示理由
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity,
                        permission
                    )
                ) {
                    if (beforeRationale.isNullOrBlank()) {
                        beforeRationale =
                            overallRationaleRes?.let { overallRationale ->
                                requireActivity.getString(overallRationale)
                            }
                        beforeRationalePermissions.add(permission)
                    }
                    if (it.rationaleRes != null) {
                        if (beforeRationale.isNullOrBlank()) {
                            beforeRationale = requireActivity.getString(it.rationaleRes)
                        } else {
                            beforeRationale += "\n${requireActivity.getString(it.rationaleRes)}"
                        }
                        beforeRationalePermissions.add(permission)
                    }
                }
            }
        }
        // 已全部同意，不需要再请求
        if (deniedPermissions.isEmpty()) {
            onResult(true, permissionResult)
            return
        }
        // 请求后的回调
        onLauncherResultCallback = { launcherResult ->
            onLaunchResult(
                launcherResult,
                permissionResult,
                permissionRationales,
                overallRationaleRes,
                onResult
            )
        }
        // 自定义回调的生命周期管理
        lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                lifecycle.removeObserver(this)
                onLauncherResultCallback = null
                super.onDestroy(owner)
            }
        })
        // 开始请求
        if (beforeRationale.isNullOrBlank()) {
            currentRequestHasShownRationalePermissions = null
            launcher?.launch(deniedPermissions.toTypedArray())
        } else {
            // 显示理由
            showAlertDialog(
                requireActivity,
                beforeRationale!!,
                R.string.arc_fast_ok.string,
                {
                    // 确认
                    currentRequestHasShownRationalePermissions = beforeRationalePermissions
                    launcher?.launch(deniedPermissions.toTypedArray())
                },
                R.string.arc_fast_cancel.string,
                {
                    // 取消
                    onLauncherResultCallback = null
                    onResult(false, permissionResult)
                }
            )
        }
    }

    private fun onLaunchResult(
        launcherResult: Map<String, Boolean>,
        permissionResult: HashMap<String, PermissionResult>,
        permissionRationales: HashMap<String, Int>,
        @StringRes overallRationaleRes: Int? = null,
        onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit
    ) {
        val requireActivity = activity ?: return
        var donTAskAgainRationale: String? = null
        launcherResult.forEach {
            permissionResult[it.key] = if (it.value) {
                // 同意授予权限
                PermissionResult.Granted
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity,
                    it.key
                )
            ) {
                if (currentRequestHasShownRationalePermissions?.contains(it.key) != true) {
                    if (donTAskAgainRationale.isNullOrBlank()) donTAskAgainRationale =
                        overallRationaleRes?.let { overallRationale ->
                            requireActivity.getString(overallRationale)
                        }
                    val rationaleRes = permissionRationales[it.key]
                    if (rationaleRes != null) {
                        if (donTAskAgainRationale.isNullOrBlank()) donTAskAgainRationale =
                            requireActivity.getString(rationaleRes)
                        else donTAskAgainRationale += "\n${requireActivity.getString(rationaleRes)}"
                    }
                }
                // 拒绝授予权限，且不允许再询问
                PermissionResult.DeniedAndDonTAskAgain
            } else {
                // 拒绝授予权限
                PermissionResult.Denied
            }
        }
        // 回调
        onResult.invoke(
            permissionResult.values.firstOrNull { it != PermissionResult.Granted } == null,
            permissionResult
        )
        if (!donTAskAgainRationale.isNullOrBlank()) {
            // 拒绝授予权限，且不允许再询问时，弹出提示
            showAlertDialog(
                requireActivity,
                donTAskAgainRationale!!,
                R.string.arc_fast_setting.string,
                {
                    // 确认
                    requireActivity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", requireActivity.packageName, null)
                    })
                },
                R.string.arc_fast_cancel.string,
                {}
            )
        }
    }

    fun registerReleaseByLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                lifecycle.removeObserver(this)
                release()
                super.onDestroy(owner)
            }
        })
    }

    fun release() {
        launcher = null
        onLauncherResultCallback = null
        activity = null
        fragment = null
        defaultLifecycle = null
    }

    companion object {

        // 弹窗
        var showAlertDialog: (
            activity: FragmentActivity,
            message: String,
            positiveButton: String,
            onPositiveButton: () -> Unit,
            negativeButton: String,
            onNegativeButton: () -> Unit
        ) -> Unit =
            { activity, message, positiveButton, onPositiveButton, negativeButton, onNegativeButton ->
                MaterialAlertDialogBuilder(activity)
                    .setMessage(message)
                    .setNegativeButton(negativeButton) { _, _ -> onNegativeButton.invoke() }
                    .setPositiveButton(positiveButton) { _, _ -> onPositiveButton.invoke() }
                    .show()
            }

        // 通过快捷方式创建的实例
        private val fastInstances by lazy {
            HashMap<Int, PermissionUtil>()
        }

        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            fragment: Fragment,
            permission: String, //Manifest.permission.xxx
            @StringRes rationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
        ) {
            fastRequest(
                activity = fragment.requireActivity(),
                requestLifecycle = fragment.lifecycle,
                PermissionRequest(permission, rationaleRes),
                onResult = onResult
            )
        }

        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            fragment: Fragment,
            vararg permissions: String, //Manifest.permission.xxx
            @StringRes overallRationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
        ) {
            fastRequest(
                activity = fragment.requireActivity(),
                requestLifecycle = fragment.lifecycle,
                permissionRequests = permissions.map {
                    PermissionRequest(permission = it)
                }.toTypedArray(),
                overallRationaleRes = overallRationaleRes,
                onResult = onResult
            )
        }

        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            fragment: Fragment,
            requestLifecycle: Lifecycle?, //本次请求的生命周期
            vararg permissionRequests: PermissionRequest,
            @StringRes overallRationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit
        ) {
            fastRequest(
                activity = fragment.requireActivity(),
                requestLifecycle = requestLifecycle,
                permissionRequests = permissionRequests,
                overallRationaleRes = overallRationaleRes,
                onResult = onResult
            )
        }


        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            activity: FragmentActivity,
            permission: String, //Manifest.permission.xxx
            @StringRes rationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
        ) {
            fastRequest(
                activity = activity,
                requestLifecycle = null,
                PermissionRequest(permission, rationaleRes),
                onResult = onResult
            )
        }

        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            activity: FragmentActivity,
            vararg permissions: String, //Manifest.permission.xxx
            @StringRes overallRationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit //allGranted,results
        ) {
            fastRequest(
                activity = activity,
                requestLifecycle = null,
                permissionRequests = permissions.map {
                    PermissionRequest(permission = it)
                }.toTypedArray(),
                overallRationaleRes = overallRationaleRes,
                onResult = onResult
            )
        }

        /**
         * 快速请求权限
         * 该方式为通过创建一个空fragment，实现懒加载式PermissionUtil权限启动器；
         * 该方式所创建的权限启动器会自动缓存，通常一个activity实例只会创建一个权限启动器，并自动跟随生命周期销毁
         * 如果您不想要创建空fragment，您应该考虑自己创建PermissionUtil实例
         */
        @JvmStatic
        fun fastRequest(
            activity: FragmentActivity,
            requestLifecycle: Lifecycle?, //本次请求的生命周期
            vararg permissionRequests: PermissionRequest,
            @StringRes overallRationaleRes: Int? = null,
            onResult: (allGranted: Boolean, result: Map<String, PermissionResult>) -> Unit
        ) {
            // 先判断当前是否已经拥有权限，有的话不需要判断实例
            val checkResult = HashMap<String, PermissionResult>()
            if (permissionRequests.firstOrNull { request ->
                    ContextCompat.checkSelfPermission(
                        activity,
                        request.permission
                    ).let {
                        if (it == PackageManager.PERMISSION_GRANTED) {
                            checkResult[request.permission] = PermissionResult.Granted
                            false
                        } else true
                    }
                } == null) {
                onResult.invoke(true, checkResult)
                return
            }
            // 先判断是否有快速实例的缓存
            var instance = fastInstances[activity.hashCode()]
            // 判断缓存是否可用
            if (instance != null && !instance.isReady) {
                fastInstances.remove(activity.hashCode())
                instance = null
            }
            // 无可用缓存实例，重新创建
            if (instance == null) {
                // 创建一个空的Fragment，以便registerForActivityResult
                activity.supportFragmentManager.beginTransaction()
                    .add(FastPermissionFragment { fragment, permissionUtil ->
                        // 缓存
                        fastInstances[activity.hashCode()] = permissionUtil
                        // 请求权限
                        permissionUtil.request(
                            lifecycle = requestLifecycle,
                            permissionRequests = permissionRequests,
                            overallRationaleRes = overallRationaleRes,
                            onResult = onResult
                        )
                        // 注册生命周期自动清理缓存
                        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                fragment.lifecycle.removeObserver(this)
                                fastInstances.remove(activity.hashCode())
                                super.onDestroy(owner)
                            }
                        })
                    }, null).commit()
            } else {
                // 有可用的可用缓存实例，直接请求权限
                instance.request(
                    lifecycle = requestLifecycle,
                    permissionRequests = permissionRequests,
                    overallRationaleRes = overallRationaleRes,
                    onResult = onResult
                )
            }
        }

        @JvmStatic
        fun check(
            context: Context,
            vararg permissions: String, //Manifest.permission.xxx
        ): Boolean = permissions.firstOrNull {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        } == null
    }
}

class FastPermissionFragment(val onFast: (Fragment, PermissionUtil) -> Unit) :
    Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onFast.invoke(this, PermissionUtil(this))
    }
}

/**
 * 权限请求结果
 */
enum class PermissionResult {
    /**
     * 同意授予权限
     */
    Granted,

    /**
     * 拒绝授予权限
     */
    Denied,

    /**
     * 拒绝授予权限，且不允许再询问
     */
    DeniedAndDonTAskAgain
}

/**
 * 权限请求
 */
data class PermissionRequest(
    /**
     * 权限
     * Manifest.permission.xxx
     */
    val permission: String,
    /**
     * 权限理由说明
     */
    @StringRes val rationaleRes: Int? = null
)