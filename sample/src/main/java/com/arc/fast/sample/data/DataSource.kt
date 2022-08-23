package com.arc.fast.sample.data

import com.arc.fast.sample.data.entity.ApiResult
import com.arc.fast.sample.data.entity.Response
import com.arc.fast.sample.extension.LOG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class DataSource {
    val networkAPI: NetworkAPI

    // httpClient
    val httpClient: OkHttpClient

    init {
        // 添加公共Header
        httpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()

            val newRequest = request.newBuilder().apply {
                // 替换为选中的环境
                if (!LocalData.currentEnvironmentValue.contains(request.url.host, true)) {
                    val newUrl = LocalData.currentEnvironmentValue.toHttpUrl()
                    url(
                        request.url.newBuilder()
                            .scheme(newUrl.scheme)
                            .host(newUrl.host)
                            .port(newUrl.port)
                            .build()
                    )
                }
                // 添加Header
//                addHeader(
//                    DATA_INTERFACE_HEADER_KEY_CONTENT_TYPE,
//                    DATA_INTERFACE_HEADER_KEY_CONTENT_TYPE_VALUE
//                ).build()
            }.build()
            chain.proceed(newRequest)
        }.addInterceptor(LimitHttpLoggingInterceptor(logger = object :
            LimitHttpLoggingInterceptor.Logger {
            // 拦截接口数据
            override fun log(message: String) {
                LOG("DataSource：$message")
            }
        }).apply {
            level = LimitHttpLoggingInterceptor.Level.BODY
        }).connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(LocalData.currentEnvironmentValue)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())//faultToleranceGson
            .build();
        networkAPI = retrofit.create(NetworkAPI::class.java)
    }

    fun login(
        email: String,
        password: String,
        sessionID: String? = null,
        checkCode: String? = null
    ) = toApiResultFlow {
        networkAPI.login(email, password, sessionID, checkCode)
    }

    fun menuList() = toApiResultFlow {
        networkAPI.menuList()
    }

    fun appUpdate() = toApiResultFlow {
        networkAPI.appUpdate()
    }

    fun verificationImag(sessionID: String?): String {
        return LocalData.currentEnvironmentValue +
                "/app/captcha.php?sessionid=" +
                sessionID +
                "&random=" +
                (10000000 until 100000000).random()
    }

}

fun <T> toApiResultFlow(onResponse: suspend () -> Response<T>): Flow<ApiResult<T>> {
    return flow {
        emit(ApiResult.Loading<T>())
        try {
            val response = onResponse()
            if (response.isSuccessful) {
                emit(ApiResult.Success(response, response.data, response.msg))
            } else {
                emit(ApiResult.Error(response, response.msg))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error<T>(null, e.toString()))
        }
    }.flowOn(Dispatchers.IO)
}

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}

