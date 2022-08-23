package com.arc.fast.core.extensions

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose


/**
 * Json转对象
 */
inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)

/**
 * Json转对象
 */
inline fun <reified T> Gson.tryFromJson(json: String?): T? = try {
    if (json.isNullOrBlank()) null
    else fromJson(json, T::class.java)
} catch (e: java.lang.Exception) {
    null
}

/**
 * 添加序列化排除策略和反序列化排除策略 匹配Gson的@Expose注解，同时实现未配置@Expose的属性全部自动匹配
 */
fun GsonBuilder.addExclusionStrategy(): GsonBuilder {
    return addSerializationExclusionStrategy().addDeserializationExclusionStrategy()
}

/**
 * 添加序列化排除策略 匹配Gson的@Expose注解，同时实现未配置@Expose的属性全部自动匹配
 */
fun GsonBuilder.addSerializationExclusionStrategy(): GsonBuilder {
    return addSerializationExclusionStrategy(
        object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                val expose = f?.getAnnotation(Expose::class.java)
                return expose != null && !expose.serialize
            }

        })
}

/**
 * 添加反序列化排除策略 匹配Gson的@Expose注解，同时实现未配置@Expose的属性全部自动匹配
 */
fun GsonBuilder.addDeserializationExclusionStrategy(): GsonBuilder {
    return addDeserializationExclusionStrategy(
        object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                val expose =
                    f?.getAnnotation(Expose::class.java)
                // 排除字段返回true
                return expose != null && !expose.deserialize
            }

        })
}
