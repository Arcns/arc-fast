package com.arc.fast.core.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import com.arc.fast.core.FastCore
import com.arc.fast.core.file.isAvailableFile
import com.arc.fast.core.file.mkdirIfNotExists
import com.arc.fast.core.file.splicingFilePath
import com.arc.fast.core.util.invokeOrNull
import java.io.*

/**
 * 文件转bitmap
 */
fun String?.toBitmap(
    opts: BitmapFactory.Options? = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
): Bitmap? =
    if (isNullOrBlank()) null else invokeOrNull {
        BitmapFactory.decodeFile(this, opts)
    }

/**
 * 资源转bitmap
 */
fun Int?.toBitmap(
    opts: BitmapFactory.Options? = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
): Bitmap? =
    if (this == null) null else invokeOrNull {
        BitmapFactory.decodeResource(FastCore.context.resources, this, opts)
    }

/**
 * 流转bitmap
 */
fun InputStream?.toBitmap(
    opts: BitmapFactory.Options? = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
): Bitmap? =
    if (this == null) null else invokeOrNull {
        BitmapFactory.decodeStream(this, null, opts)
    }

/**
 * 文件转bitmap
 */
fun File?.toBitmap(
    opts: BitmapFactory.Options? = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
): Bitmap? =
    if (!isAvailableFile) null else invokeOrNull {
        FileInputStream(this).toBitmap(opts)
    }

/**
 * Uri转bitmap
 */
fun Uri?.toBitmap(
    opts: BitmapFactory.Options? = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
): Bitmap? = if (this == null) null else invokeOrNull {
    FastCore.context.contentResolver.openInputStream(this).toBitmap(opts)
}

/**
 * bitmap转base64
 */
fun Bitmap?.toBase64(): String? {
    if (this == null) return null
    var result: String? = null
    var outputStream: ByteArrayOutputStream? = null
    try {
        outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        result = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        outputStream?.flush()
        outputStream?.tryClose()
    }
    return result
}

/**
 * 旋转bitmap
 */
fun Bitmap.rotate(degrees: Float): Bitmap = Bitmap.createBitmap(
    this,
    0,
    0,
    width,
    height,
    Matrix().apply { postRotate(degrees) },
    true
)

/**
 * 把Bitmap另存为文件
 */
fun Bitmap?.saveAsFile(context: Context?, toFileDir: String?, toFileName: String?): String? {
    if (this == null || context == null || toFileDir.isNullOrBlank() || toFileName.isNullOrBlank()) {
        return null
    }
    val toFile = File(toFileDir.apply {
        // 判断目标目录是否存在
        mkdirIfNotExists()
    }.splicingFilePath(toFileName))
    var outputStream: OutputStream? = null
    try {
        outputStream = FileOutputStream(toFile)
        compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return toFile.absolutePath
    } catch (e: Exception) {
        return null
    } finally {
        outputStream.tryClose()
    }
}

