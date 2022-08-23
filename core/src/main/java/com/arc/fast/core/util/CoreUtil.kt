package com.arc.fast.core.util

import android.os.Looper


/**
 * 执行时如果捕获到异常就返回null
 */
fun <T> invokeOrNull(execute: () -> T): T? = try {
    execute()
} catch (e: Exception) {
    null
}

/**
 * 执行时忽略捕获到的异常
 */
fun tryInvoke(execute: () -> Unit): Boolean = try {
    execute()
    true
} catch (e: Exception) {
    false
}

/**
 * 当前是否为主线程
 */
val isMainThread: Boolean get() = Looper.getMainLooper() == Looper.myLooper()

fun notNullOrBlankElse(value: String?, elseValue: String): String =
    if (value.isNullOrBlank()) elseValue else value