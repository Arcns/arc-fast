package com.arc.fast.core.util

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.arc.fast.core.FastCore
import com.arc.fast.core.extensions.tryClose
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
val String?.isAvailableFile: Boolean get() = !isNullOrBlank() && File(this).let { it.exists() && it.isFile }

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
fun Uri?.getFileInfo(): UriFileInfo {
    return getFileInfo(
        arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATA
        )
    ).let {
        UriFileInfo(
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
fun Uri?.getFileInfo(
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
 * Uri对应文件信息
 */
data class UriFileInfo(
    /**
     * 名称
     */
    var displayName: String?,
    /**
     * 类型
     */
    var mimeType: String?,
    /**
     * 大小
     */
    var size: String?,
    /**
     * 路径
     */
    var path: String?
)

fun String?.copyFile(
    toFileDir: String,
    toFileName: String? = null
): String? {
    if (isNullOrBlank()) return null
    return copyFile(toFileDir, toFileName)
}

/**
 * 复制文件
 */
@JvmOverloads
fun File.copyFile(
    toFileDir: String?,
    toFileName: String? = null
): String? {
    if (toFileDir.isNullOrBlank() && toFileName.isNullOrBlank()) {
        return null
    }
    val fromFile = File(fromFilePath)
    if (!fromFile.exists()) {
        return null
    }
    // 默认目标目录为原目录
    if (isEmptyOrBlank(toFileDir)) {
        toFileDir = getFileDirectory(fromFilePath)
    }
    // 默认新文件名为原文件名
    if (isEmptyOrBlank(toFileName)) {
        toFileName = fromFile.name
    }
    // 判断目标目录是否存在
    mkdirIfNotExists(toFileDir)
    // 创建目标文件
    toFileDir = getSupplementaryDirPath(toFileDir)
    val toFile = File(toFileDir + toFileName)
    // 开始复制
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    try {
        inputStream = FileInputStream(fromFile)
        outputStream = FileOutputStream(toFile)
        var byteCount = 0
        val bytes = ByteArray(1024)
        while (inputStream.read(bytes).also { byteCount = it } != -1) {
            outputStream.write(bytes, 0, byteCount)
        }
        return toFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        tryClose(inputStream)
        tryClose(outputStream)
    }
    return null
}

object FileUtil {


    /**
     * 复制文件
     */
    @JvmOverloads
    fun copyFile(
        fromFilePath: String,
        toFileDir: String,
        toFileName: String? = null
    ): String? {
        var toFileDir = toFileDir
        var toFileName = toFileName
        if (isEmptyOrBlank(fromFilePath)) {
            return null
        }
        if (isEmptyOrBlank(toFileDir) && isEmptyOrBlank(toFileName)) {
            return null
        }
        val fromFile = File(fromFilePath)
        if (!fromFile.exists()) {
            return null
        }
        // 默认目标目录为原目录
        if (isEmptyOrBlank(toFileDir)) {
            toFileDir = getFileDirectory(fromFilePath)
        }
        // 默认新文件名为原文件名
        if (isEmptyOrBlank(toFileName)) {
            toFileName = fromFile.name
        }
        // 判断目标目录是否存在
        mkdirIfNotExists(toFileDir)
        // 创建目标文件
        toFileDir = getSupplementaryDirPath(toFileDir)
        val toFile = File(toFileDir + toFileName)
        // 开始复制
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = FileInputStream(fromFile)
            outputStream = FileOutputStream(toFile)
            var byteCount = 0
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { byteCount = it } != -1) {
                outputStream.write(bytes, 0, byteCount)
            }
            return toFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tryClose(inputStream)
            tryClose(outputStream)
        }
        return null
    }

    /**
     * 复制文件
     */
    @JvmOverloads
    fun copyFile(
        inputStream: InputStream?,
        toFileDir: String,
        toFileName: String,
        progressListener: ProgressListener? = null
    ): String? {
        var toFileDir = toFileDir
        if (inputStream == null) {
            return null
        }
        if (isEmptyOrBlank(toFileDir) || isEmptyOrBlank(toFileName)) {
            return null
        }
        // 判断目标目录是否存在
        mkdirIfNotExists(toFileDir)
        // 创建目标文件
        toFileDir = getSupplementaryDirPath(toFileDir)
        val toFile = File(toFileDir + toFileName)
        // 开始复制
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(toFile)
            var totalCount = -1.0
            try {
                totalCount = (inputStream as FileInputStream).channel.size().toDouble()
            } catch (e: Exception) {
            }
            var cumulativeCount = 0.0
            var byteCount = 0
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { byteCount = it } != -1) {
                outputStream.write(bytes, 0, byteCount)
                if (progressListener != null && totalCount != -1.0) {
                    cumulativeCount += byteCount.toDouble()
                    val progress = (cumulativeCount / totalCount * 100).toInt()
                    progressListener.update(progress)
                }
            }
            return toFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tryClose(inputStream)
            tryClose(outputStream)
        }
        return null
    }

    /**
     * 复制文件
     */
    @JvmOverloads
    fun copyFile(
        fromFilePath: String?,
        outputStream: OutputStream?,
        progressListener: ProgressListener? = null
    ): Boolean {
        if (isEmptyOrBlank(fromFilePath)) {
            return false
        }
        if (outputStream == null) {
            return false
        }
        val fromFile = File(fromFilePath)
        if (!fromFile.exists()) {
            return false
        }
        // 开始复制
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(fromFile)
            val totalCount = fromFile.length().toDouble()
            var cumulativeCount = 0.0
            var byteCount = 0
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { byteCount = it } != -1) {
                outputStream.write(bytes, 0, byteCount)
                if (progressListener != null) {
                    cumulativeCount += byteCount.toDouble()
                    val progress = (cumulativeCount / totalCount * 100).toInt()
                    progressListener.update(progress)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tryClose(inputStream)
            tryClose(outputStream)
        }
        return false
    }

    /**
     * 复制文件夹
     */
    fun copyDir(fromDirPath: String?, toDirPath: String) {
        var toDirPath = toDirPath
        if (isEmptyOrBlank(fromDirPath) || isEmptyOrBlank(toDirPath)) {
            return
        }
        val fromDir = File(fromDirPath)
        if (!fromDir.exists() && !fromDir.isDirectory) {
            return
        }
        val childFiles = fromDir.listFiles()
        if (childFiles == null || childFiles.size == 0) {
            return
        }
        mkdirIfNotExists(toDirPath)
        toDirPath = getSupplementaryDirPath(toDirPath)
        for (childFile in childFiles) {
            if (childFile.isDirectory) {
                copyDir(childFile.absolutePath, toDirPath + childFile.name)
            } else {
                copyFile(childFile.absolutePath, toDirPath)
            }
        }
    }
    /**
     * 剪切文件
     */
    /**
     * 剪切文件
     */
    @JvmOverloads
    fun moveFile(
        fromFilePath: String,
        toFileDir: String,
        toFileName: String? = null
    ): String? {
        if (isEmptyOrBlank(fromFilePath) || isEmptyOrBlank(toFileDir)) {
            return null
        }
        val toFilePath = copyFile(fromFilePath, toFileDir, toFileName)
        if (!isEmptyOrBlank(toFilePath)) {
            removeFile(fromFilePath)
            return toFilePath
        }
        return null
    }

    /**
     * 剪切文件
     */
    fun moveDir(fromDirPath: String, toDirPath: String) {
        if (isEmptyOrBlank(fromDirPath) || isEmptyOrBlank(toDirPath)) {
            return
        }
        copyFile(fromDirPath, toDirPath)
        removeFile(fromDirPath)
    }

    /**
     * 把Uri文件保存到指定地址中
     */
    fun saveFileWithUri(context: Context?, fromUri: Uri?, toFilePath: String): Boolean {
        return saveFileWithUri(
            context,
            fromUri,
            getFileDirectory(toFilePath),
            getFileName(toFilePath)
        )
    }


    /**
     * 把Uri文件保存到指定地址中
     */
    fun saveFileWithUri(
        context: Context?,
        fromUri: Uri?,
        toFileDir: String,
        toFileName: String
    ): Boolean {
        var toFileDir = toFileDir
        if (context == null || fromUri == null || isEmptyOrBlank(toFileDir) || isEmptyOrBlank(
                toFileName
            )
        ) {
            return false
        }
        mkdirIfNotExists(toFileDir)
        toFileDir = getSupplementaryDirPath(toFileDir)
        val autoSaveImage = File(toFileDir + toFileName)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(fromUri)
            outputStream = FileOutputStream(autoSaveImage)
            var byteCount = 0
            val bytes = ByteArray(1024)
            while (inputStream!!.read(bytes).also { byteCount = it } != -1) {
                outputStream.write(bytes, 0, byteCount)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tryClose(inputStream)
            tryClose(outputStream)
        }
        return false
    }

    /**
     * 判断文件类型（通过魔数）
     */
    @Throws(IOException::class)
    fun getType(filePath: String): FileType? {
        // 获取文件头
        var fileHead = getFileHeader(filePath)
        if (fileHead != null && fileHead.length > 0) {
            fileHead = fileHead.uppercase(Locale.getDefault())
            val fileTypes: Array<FileType> = FileType.values()
            for (type in fileTypes) {
                if (fileHead.startsWith(type.getValue())) {
                    return type
                }
            }
        }
        return null
    }

    /**
     * 读取文件头
     */
    @Throws(IOException::class)
    private fun getFileHeader(filePath: String): String? {
        val b = ByteArray(28)
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(filePath)
            inputStream.read(b, 0, 28)
        } finally {
            tryClose(inputStream)
        }
        return bytesToHex(b)
    }

    /**
     * 将字节数组转换成16进制字符串
     */
    fun bytesToHex(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.size <= 0) {
            return null
        }
        for (i in src.indices) {
            val v: Int = src[i] and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }


    interface ProgressListener {
        fun update(progress: Int)
    }
}