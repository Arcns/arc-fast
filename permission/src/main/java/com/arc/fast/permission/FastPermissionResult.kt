package com.arc.fast.permission

/**
 * 权限请求结果
 */
enum class FastPermissionResult {
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
