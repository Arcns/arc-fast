package com.arc.fast.core.file

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.arc.fast.core.FastCore
import com.arc.fast.core.extensions.tryClose
import com.arc.fast.core.util.notNullOrBlankElse
import java.io.*
import java.util.*


/**
 * 检查文件是否为某个后缀名（不区分大小写）
 */
fun String?.checkFileSuffix(vararg suffix: String): Boolean =
    if (isNullOrBlank()) false else suffix.firstOrNull { endsWith(it, true) } != null

/**
 * 获取文件后缀名(带.)
 */
val String?.fileSuffixOrNull: String?
    get() = if (isNullOrBlank()) null else if (contains(".")) substring(
        lastIndexOf(".")
    ) else null

/**
 * 获取文件后缀名(带.)
 */
val String?.fileSuffix: String get() = fileSuffixOrNull ?: ""

/**
 * 获取文件的目录部分（结尾带/）
 */
val String?.fileDirectoryOrNull: String?
    get() = if (isNullOrBlank()) null
    else if (contains(File.separator)) substring(0, lastIndexOf(File.separator))
    else null

/**
 * 获取文件的目录部分（结尾带/）
 */
val String?.fileDirectory: String get() = fileDirectoryOrNull ?: ""

/**
 * 获取文件名
 */
val String?.fileNameOrNull: String?
    get() = if (isNullOrBlank()) null
    else if (contains(File.separator)) substring(lastIndexOf(File.separator) + 1)
    else this

/**
 * 获取文件名
 */
val String?.fileName: String get() = fileNameOrNull ?: ""

/**
 * 获取文件名（不带后缀名）
 */
val String?.fileNameNotSuffixOrNull: String?
    get() = fileNameOrNull?.let {
        if (it.contains(".")) {
            it.substring(0, it.lastIndexOf('.'))
        } else {
            it
        }
    }

/**
 * 获取文件名（不带后缀名）
 */
val String?.fileNameNotSuffix: String get() = fileNameNotSuffixOrNull ?: ""

/**
 * 补全文件夹路径（确保最后字符是/）
 */
val String?.supplementaryFileDirPathOrNull: String?
    get() = if (isNullOrBlank()) null
    else if (!endsWith(File.separator)) this + File.separator else this

/**
 * 补全文件夹路径（确保最后字符是/）
 */
val String?.supplementaryFileDirPath: String get() = supplementaryFileDirPathOrNull ?: ""

/**
 * 拼接文件路径（会自动确保拼接字符是/）
 */
fun String?.splicingFilePath(fileName: String): String {
    return supplementaryFileDirPath + fileName
}

/**
 * 删除文件或目录（包含目录下文件）
 */
fun File.removeFile() {
    if (!exists()) return
    //如果是文件直接删除
    if (isFile) {
        delete()
        return
    }
    //如果是目录，递归判断，如果是空目录，直接删除，如果是文件，遍历删除
    if (isDirectory) {
        val childFiles = listFiles()
        if (childFiles.isNullOrEmpty()) {
            delete()
            return
        }
        childFiles.forEach {
            it.removeFile()
        }
        delete()
    }
}

/**
 * 删除文件或目录（包含目录下文件）
 */
fun String?.removeFile() {
    if (isNullOrBlank()) return
    File(this).removeFile()
}

/**
 * 创建文件夹（如果不存在的话）
 */
fun String?.mkdirIfNotExists() {
    if (isNullOrBlank()) return
    val file = File(this)
    if (!file.exists()) {
        file.mkdirs()
    }
}

/**
 * 检查是否为可用的文件（已存在且为文件）
 */
val String?.isAvailableFile: Boolean get() = !isNullOrBlank() && File(this).isAvailableFile


/**
 * 检查是否为可用的文件（已存在且为文件）
 */
val File?.isAvailableFile: Boolean get() = this?.exists() == true && this.isFile

/**
 * 读取文件中的文本内容
 */
fun String?.readFileContent(): String? {
    if (!isAvailableFile) return null
    var bufferedReader: BufferedReader? = null
    try {
        bufferedReader = BufferedReader(FileReader(this))
        val content = StringBuffer()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            content.append(line)
        }
        return content.toString()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        bufferedReader.tryClose()
    }
    return null
}

/**
 * 通过bufferedWriter把数据写入文件中
 */
fun writeFileViaWriterBuffered(
    toFileDir: String?,
    toFileName: String?,
    onWrite: (BufferedWriter) -> Unit
): String? {
    if (toFileDir.isNullOrBlank() || toFileName.isNullOrBlank()) return null
    // 判断目标目录是否存在
    toFileDir.mkdirIfNotExists()
    // 创建目标文件
    val toFilePath = toFileDir.supplementaryFileDirPath + toFileName
    var bufferedWriter: BufferedWriter? = null
    try {
        bufferedWriter = BufferedWriter(FileWriter(toFilePath))
        onWrite.invoke(bufferedWriter)
        return toFilePath
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        bufferedWriter.tryClose()
    }
    return null
}


/**
 * 通过OutputStream把数据写入文件中
 */
fun writeUriViaOutputStream(
    toFileUri: Uri?,
    onWrite: (FileOutputStream) -> Unit
): Boolean {
    if (toFileUri == null) return false
    // 判断目标目录是否存在
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    var fileOutputStream: FileOutputStream? = null
    try {
        parcelFileDescriptor =
            FastCore.context.contentResolver.openFileDescriptor(toFileUri, "w")
        fileOutputStream = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
        onWrite.invoke(fileOutputStream)
        return true
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        fileOutputStream.tryClose()
        parcelFileDescriptor.tryClose()
    }
    return false
}

/**
 * 把ByteArray写入文件中
 */
fun ByteArray.writeToUri(toFileUri: Uri?): Boolean =
    writeUriViaOutputStream(toFileUri) { it.write(this) }

/**
 * 把String写入文件中
 */
fun String.writeToFile(toFileDir: String?, toFileName: String?): String? =
    writeFileViaWriterBuffered(toFileDir, toFileName) { it.write(this) }


/**
 * 返回Uri的对应文件信息
 */
fun Uri?.getFileUriInfo(): FileUriInfo {
    return getFileUriInfo(
        arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATA
        )
    ).let {
        FileUriInfo(
            displayName = it[MediaStore.Files.FileColumns.DISPLAY_NAME],
            mimeType = it[MediaStore.Files.FileColumns.MIME_TYPE],
            size = it[MediaStore.Files.FileColumns.SIZE],
            path = it[MediaStore.Files.FileColumns.DATA]
        )
    }
}

/**
 * 返回Uri的对应文件信息
 */
fun Uri?.getFileUriInfo(
    infoProjection: Array<String>?
): LinkedHashMap<String, String?> {
    val values = LinkedHashMap<String, String?>()
    if (this == null || infoProjection.isNullOrEmpty()) return values
    val cursor = FastCore.context.contentResolver.query(this, infoProjection, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        for (i in infoProjection.indices) {
            val columnIndex = cursor.getColumnIndex(infoProjection[i])
            if (columnIndex >= 0) {
                try {
                    values[infoProjection[i]] = cursor.getString(columnIndex)
                } catch (e: Exception) {
                    e.printStackTrace()
                    values[infoProjection[i]] = null
                }
            } else values[infoProjection[i]] = null
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
    }
    return values
}

/**
 * 复制文件
 */
fun String?.copyFile(
    toFileDir: String,
    toFileName: String? = null,
    onProgress: ((total: Long, current: Long, progress: Double) -> Unit)? = null
): String? {
    if (isNullOrBlank()) return null
    return File(this).copyFile(toFileDir, toFileName, onProgress)
}

/**
 * 复制文件
 */
fun File.copyFile(
    toFileDir: String?,
    toFileName: String? = null,
    onProgress: ((total: Long, current: Long, progress: Double) -> Unit)? = null
): String? {
    if (toFileDir.isNullOrBlank() && toFileName.isNullOrBlank()) {
        return null
    }
    if (!isAvailableFile) {
        return null
    }
    // 开始复制
    var inputStream: InputStream? = null
    try {
        inputStream = FileInputStream(this)
        return inputStream.copyFile(
            notNullOrBlankElse(toFileDir, absolutePath.fileDirectory),
            notNullOrBlankElse(toFileName, name),
            onProgress
        )
    } catch (e: Exception) {
        e.printStackTrace()
        inputStream.tryClose()
    }
    return null
}


/**
 * 复制文件
 */
fun InputStream.copyFile(
    toFileDir: String?,
    toFileName: String?,
    onProgress: ((total: Long, current: Long, progress: Double) -> Unit)? = null
): String? {
    if (toFileDir.isNullOrBlank() || toFileName.isNullOrBlank()) {
        return null
    }
    val toFile = File(toFileDir.apply {
        // 判断目标目录是否存在
        mkdirIfNotExists()
    }.splicingFilePath(toFileName))
    // 开始复制
    var outputStream: OutputStream? = null
    try {
        outputStream = FileOutputStream(toFile)
        return if (copyFile(outputStream, onProgress)) toFile.absolutePath else null
    } catch (e: Exception) {
        outputStream.tryClose()
        e.printStackTrace()
    }
    return null
}

/**
 * 复制文件
 */
fun InputStream.copyFile(
    outputStream: OutputStream?,
    onProgress: ((total: Long, current: Long, progress: Double) -> Unit)? = null
): Boolean {
    if (outputStream == null) {
        return false
    }
    try {
        val total = (this as? FileInputStream)?.channel?.size() ?: 0L
        var current = 0L
        var byteCount = 0
        val bytes = ByteArray(1024)
        while (read(bytes).also { byteCount = it } != -1) {
            outputStream.write(bytes, 0, byteCount)
            if (onProgress != null) {
                current += byteCount
                onProgress.invoke(
                    total,
                    current,
                    if (total > 0L) current.toDouble() / total.toDouble() * 100 else 0.0
                )
            }
        }
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        tryClose()
        outputStream.tryClose()
    }
    return false
}

/**
 * 复制文件夹
 */
fun File?.copyDir(toDirPath: String?) {
    if (this == null || toDirPath.isNullOrBlank()) {
        return
    }
    if (!exists() && !isDirectory) return
    val childFiles = listFiles()
    if (childFiles.isNullOrEmpty()) {
        return
    }
    if (toDirPath.isAvailableFile) return
    toDirPath.mkdirIfNotExists()
    for (childFile in childFiles) {
        if (childFile.isDirectory) {
            childFile.copyDir(toDirPath.splicingFilePath(name))
        } else {
            copyFile(toDirPath)
        }
    }
}

/**
 * 复制文件夹
 */
fun String?.copyDir(toDirPath: String?) {
    if (isNullOrBlank()) {
        return
    }
    File(this).copyDir(toDirPath)
}

/**
 * 剪切文件
 */
fun String?.moveFile(
    toFileDir: String?,
    toFileName: String? = null,
    onProgress: ((total: Long, current: Long, progress: Double) -> Unit)? = null
): String? {
    if (isNullOrBlank() || toFileDir.isNullOrBlank()) {
        return null
    }
    val toFilePath = copyFile(toFileDir, toFileName, onProgress)
    if (!toFilePath.isNullOrBlank()) {
        this.removeFile()
        return toFilePath
    }
    return null
}

/**
 * 把Uri文件另存为文件
 */
fun Uri?.saveAsFile(context: Context?, toFileDir: String?, toFileName: String?): String? {
    if (this == null || context == null || toFileDir.isNullOrBlank() || toFileName.isNullOrBlank()) {
        return null
    }
    var inputStream: InputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(this)
        return inputStream?.copyFile(toFileDir, toFileName)
    } catch (e: Exception) {
        e.printStackTrace()
        inputStream.tryClose()
    }
    return null
}

/**
 * 获取文件魔数类型
 */
val String?.fileMagicNumberType: FileMagicNumberType?
    get() {
        if (isNullOrBlank()) return null
        var inputStream: InputStream? = null
        var fileHead = try {
            val byteArray = ByteArray(28)
            inputStream = FileInputStream(this)
            // 获取文件头
            inputStream.read(byteArray, 0, 28)
            byteArray.let { src ->
                if (src.isEmpty()) null
                else {
                    val stringBuilder = StringBuilder("")
                    for (i in src.indices) {
                        val v = src[i].toInt() and 0xFF
                        val hv = Integer.toHexString(v)
                        if (hv.length < 2) {
                            stringBuilder.append(0)
                        }
                        stringBuilder.append(hv)
                    }
                    stringBuilder.toString().uppercase()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream.tryClose()
        }
        return if (!fileHead.isNullOrBlank()) {
            fileHead = fileHead.uppercase()
            FileMagicNumberType.values().firstOrNull {
                fileHead.startsWith(it.value)
            }
        } else null
    }

/**
 * 获取文件Mime类型
 */
val String?.fileMimeType: FileMimeType get() = FileMimeType.getMimeTypeBySuffix(suffix = fileSuffixOrNull)


/**
 * 文件转base64
 */
fun File?.asBase64(): String? {
    if (!isAvailableFile) return null
    var result: String? = null
    var inputStream: FileInputStream? = null
    try {
        inputStream = FileInputStream(this)
        val bytes = ByteArray(inputStream.available())
        val length = inputStream.read(bytes)
        result = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT)
    } catch (e: java.lang.Exception) {
        e.printStackTrace();
    } finally {
        inputStream.tryClose()
    }
    return result
}

/**
 * 文件转Uri
 */
fun File.asUri(
    context: Context,
    /*The authority of a FileProvider defined in a <provider> element in your app's manifest.*/
    authority: String
): Uri? = try {
    if (!isAvailableFile) null
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, authority, this);
    } else {
        Uri.fromFile(this);
    }
} catch (e: Exception) {
    e.printStackTrace()
    null
}
