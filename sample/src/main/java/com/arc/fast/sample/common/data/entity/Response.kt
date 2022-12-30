package com.arc.fast.sample.common.data.entity

data class Response<T>(
    var result: String,
    var sessionid: String? = null,
    var errorCode: Int? = null,
    var msg: String? = null,
    var secret: String? = null,
    var data: T? = null
) {
    val isSuccessful: Boolean get() = "success".equals(result, true)
}
