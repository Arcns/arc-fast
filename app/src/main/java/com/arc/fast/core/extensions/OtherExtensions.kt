package com.arc.fast.core.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.ParcelFileDescriptor
import com.arc.fast.core.FastCore
import com.arc.fast.core.util.tryInvoke
import java.io.*

/**
 * 集合转ArrayList
 */
inline fun <reified T> Collection<T>.toArrayList(): ArrayList<T> {
    return if (this is ArrayList<T>) this else ArrayList<T>().apply {
        addAll(this@toArrayList)
    }
}

/**
 * 复制到粘贴板
 */
fun String.copyToClipboard() {
    (FastCore.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.setPrimaryClip(
        ClipData.newPlainText(null, this)
    )
}


/**
 * 是否为网络资源
 */
val String.isInternetResources: Boolean
    get() = startsWith(
        "http://",
        true
    ) || startsWith("https://", true)

/**
 * 关闭并忽略捕获到的异常
 */
fun RandomAccessFile?.tryClose() = tryInvoke { this?.close() }

/**
 * 关闭并忽略捕获到的异常
 */
fun OutputStream?.tryClose() = tryInvoke { this?.close() }

/**
 * 关闭并忽略捕获到的异常
 */
fun InputStream?.tryClose() = tryInvoke { this?.close() }

/**
 * 关闭并忽略捕获到的异常
 */
fun ParcelFileDescriptor?.tryClose() = tryInvoke { this?.close() }

/**
 * 关闭并忽略捕获到的异常
 */
fun BufferedWriter?.tryClose() = tryInvoke { this?.close() }

/**
 * 关闭并忽略捕获到的异常
 */
fun BufferedReader?.tryClose() = tryInvoke { this?.close() }