package com.arc.fast.core.file


/**
 * Uri对应文件信息
 */
data class FileUriInfo(
    /**
     * 名称
     */
    var displayName: String?,
    /**
     * 类型
     */
    var mimeType: String?,
    /**
     * 大小
     */
    var size: String?,
    /**
     * 路径
     */
    var path: String?
)