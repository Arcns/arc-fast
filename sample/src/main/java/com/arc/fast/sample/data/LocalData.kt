package com.arc.fast.sample.data

import android.content.Context
import com.arc.fast.core.extensions.tryFromJson
import com.google.gson.Gson
import com.arc.fast.sample.SampleApp
import com.arc.fast.sample.R
import com.arc.fast.sample.data.entity.ApkInfo
import com.tencent.mmkv.MMKV
import java.net.URLEncoder


object LocalData {
    val environments: Map<Int, String> by lazy {
        mapOf(
            R.string.online to "https://www.konvy.com",
            R.string.testdev to "http://testdev.dongshihuafeng.com"
        )
    }

    fun initialize(context: Context) {
        MMKV.initialize(context);
    }

    val currentEnvironmentName: Int
        get() =
            if (SampleApp.isDebug) MMKV.defaultMMKV().decodeString("currentEnvironment")
                ?.let { value -> environments.keys.firstOrNull { environments[it] == value } }
                ?: R.string.testdev
            else R.string.online

    var currentEnvironmentValue: String
        set(value) {
            MMKV.defaultMMKV().encode("currentEnvironment", value)
        }
        get() =
            if (SampleApp.isDebug) MMKV.defaultMMKV().decodeString("currentEnvironment")
                ?: environments[R.string.testdev]!!
            else environments[R.string.online]!!

    var currentLoginSecret: String?
        set(value) {
            MMKV.defaultMMKV()
                .encode("currentLoginSecret", value?.let { URLEncoder.encode(it, "UTF-8") })
        }
        get() = MMKV.defaultMMKV().decodeString("currentLoginSecret")


    var downloadedApkInfo: ApkInfo?
        set(value) {
            MMKV.defaultMMKV()
                .encode("downloadedApkInfo", if (value == null) null else Gson().toJson(value))
        }
        get() = MMKV.defaultMMKV().decodeString("downloadedApkInfo").let {
            if (it.isNullOrBlank()) null else Gson().tryFromJson(it)
        }
}
