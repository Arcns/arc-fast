package com.arc.fast.sample.data

import com.arc.fast.sample.data.entity.AppUpdate
import com.arc.fast.sample.data.entity.Menu
import com.arc.fast.sample.data.entity.Response
import retrofit2.http.*

interface NetworkAPI {

    // 登录
    @FormUrlEncoded
    @POST("app/manage/appLogin.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("sessionid") sessionID: String? = null,
        @Field("checkCode") checkCode: String? = null,
        @Field("lang") lang: String = "en_US"
    ): Response<Any?>

    // 获取菜单数据
    @GET("app/manage/menuList2.php")
    suspend fun menuList(): Response<List<Menu>?>

    // 获取版本更新
    @GET("app/manager_app_update.php")
    suspend fun appUpdate(@Query("lang") lang: String = "en"): Response<AppUpdate?>

}

