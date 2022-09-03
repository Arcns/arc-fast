package com.arc.fast.permission

/**
 * 权限请求
 */
data class FastPermissionRequest(
    /**
     * 权限
     * Manifest.permission.xxx
     */
    val permission: String,
    /**
     * 权限理由说明
     */
    val rationale: String? = null
)