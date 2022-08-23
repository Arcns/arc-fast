package com.arc.fast.sample.data.entity

import com.arc.fast.sample.data.ApiStatus

sealed class ApiResult<T>(
    val status: ApiStatus,
    val response: Response<T>?,
    val data: T?,
    val message: String?
) {

    class Success<R>(response: Response<R>, data: R?, message: String? = null) :
        ApiResult<R>(
            status = ApiStatus.SUCCESS,
            response = response,
            data = data,
            message = message
        )

    class Error<R>(response: Response<R>?, error: String?) : ApiResult<R>(
        status = ApiStatus.ERROR,
        response = response,
        data = null,
        message = error
    )

    class Loading<R>() : ApiResult<R>(
        status = ApiStatus.LOADING,
        response = null,
        data = null,
        message = null
    )

    val isLoading: Boolean get() = status == ApiStatus.LOADING
}