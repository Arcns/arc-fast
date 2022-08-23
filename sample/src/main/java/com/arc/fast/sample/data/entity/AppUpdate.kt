package com.arc.fast.sample.data.entity

data class AppUpdate(
    var status: Int = 0,
    var version_code: Long = 0,
    var version_name: String? = null,
    var title: String? = null,
    var content: String? = null,
    var apk_url: String? = null,
    var apk_size: Long = 0,
    var apk_size_text: String? = null
) {


    companion object {
        /**
         * 没有更新
         */
        const val STATUS_NOT_UPDATE = 0

        /**
         * 更新不提示
         */
        const val STATUS_UPDATE = 1

        /**
         * 建议更新
         */
        const val STATUS_RECOMMEND_UPDATE = 2

        /**
         * 强制更新
         */
        const val STATUS_FORCE_UPDATE = 3
    }
}