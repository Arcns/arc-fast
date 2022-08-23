package com.arc.fast.sample.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_REASON
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.edit
import com.arc.fast.core.extensions.openBrowser
import com.arc.fast.core.extensions.tryFromJson
import com.google.gson.Gson
import com.arc.fast.sample.SampleApp
import org.greenrobot.eventbus.EventBus
import java.io.Serializable

val DOWNLOAD_SAVE_NAME_SUFFIX_APK = ".apk"

/**
 * 简单下载任务类（会通过系统服务调用系统自带的下载器进行下载，适用于简单下载场景）
 */
data class SimpleDownloadTask(
    // 下载地址
    val downloadUrl: String,
    // 下载标题
    val downloadTitle: String,
    // 下载简介
    val downloadDescription: String,
    // 下载保存文件夹（默认为O以上公共目录下载文件夹、O以下私有目录下载文件夹）
    var downloadSaveDir: String = Environment.DIRECTORY_DOWNLOADS,
    // 下载保存文件名（默认为当前时间戳）
    var downloadSaveName: String? = null,
    // 下载保存文件后缀名（默认为空）
    var downloadSaveNameSuffix: String? = null,
    // 是否允许重复下载，默认为不允许
    var isAllowDuplicate: Boolean = false,
    // 下载完成后是否自动打开
    var isAutoOpen: Boolean = false,
    // 下载错误提示
    val downloadFailedTips: String? = null,
    // 任务中可携带的自定义数据
    val extraLongData: Long? = null,
    val extraStringData: String? = null
) : Serializable {
    var downloadId: Long? = null
    var downloadStatus: Int? = null
}

/**
 * 服务状态
 */
data class SimpleDownloadTaskStatus(
    var id: Long?,
    var state: Int?,
    var reason: Int?,
    var soFarBytes: Long?, //已下载字节数
    var totalSizeBytes: Long?//总字节数
) {
    /**
     * 任务是否存在
     */
    val hasExists: Boolean
        get() {
            if (id == null) return false
            when (state) {
                DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING, DownloadManager.STATUS_RUNNING -> return true
            }
            return false
        }

    /**
     * 进度百分比
     */
    val progressPercentage: Int
        get() = (progressDecimal * 100).toInt()

    /**
     * 进度（小数）
     */
    val progressDecimal: Double
        get() =
            if (state == DownloadManager.STATUS_SUCCESSFUL) 1.0
            else if (soFarBytes == null || totalSizeBytes == null) 0.0
            else if (soFarBytes == totalSizeBytes) 1.0
            else soFarBytes!!.toDouble() / totalSizeBytes!!.toDouble()
}

/**
 * 简单下载工具类（会通过系统服务调用系统自带的下载器进行下载，适用于简单下载场景，注意Q及以上版本下载保存路径必须为公共目录否则无进度通知栏）
 */
class SimpleDownloadUtil(var context: Context) {

    private var downloadTasks = HashMap<String, SimpleDownloadTask>()
    private var downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // 缓存的下载任务ID列表（url,id）
    private val DB_CACHED_DOWNLOAD_IDS = "DB_CACHED_DOWNLOAD_IDS"
    private val KEY_CACHED_DOWNLOAD_IDS = "KEY_CACHED_DOWNLOAD_IDS"
    private var cachedDownloadIDs = HashMap<String, Long>()

    init {
        initCachedDownloadIDs()
    }

    /**
     * 获取缓存的下载任务ID列表
     */
    private fun initCachedDownloadIDs() {
        val value =
            SampleApp.instance.getSharedPreferences(DB_CACHED_DOWNLOAD_IDS, Context.MODE_PRIVATE)
                .getString(
                    KEY_CACHED_DOWNLOAD_IDS,
                    null
                )
        val ids: HashMap<String, Double>? = Gson().tryFromJson(value)
        ids?.forEach {
            // 把仍然正在进行中的下载任务添加到缓存列表中
            if (checkDownloadTaskHasExistsByID(it.value.toLong())) {
                cachedDownloadIDs[it.key] = it.value.toLong()
            }
        }
        saveCachedDownloadIDs()
    }

    /**
     * 缓存下载任务ID列表
     */
    private fun saveCachedDownloadIDs() =
        SampleApp.instance.getSharedPreferences(
            DB_CACHED_DOWNLOAD_IDS,
            Context.MODE_PRIVATE
        ).edit {
            putString(KEY_CACHED_DOWNLOAD_IDS, Gson().toJson(cachedDownloadIDs))
        }

    /**
     * 添加任务ID到缓存列表
     */
    private fun addCachedDownloadID(downloadTask: SimpleDownloadTask) {
        cachedDownloadIDs[downloadTask.downloadUrl] = downloadTask.downloadId ?: return
        saveCachedDownloadIDs()
    }

    /**
     * 获取任务ID
     */
    fun getDownloadTaskID(downloadUrl: String): Long? = downloadTasks[downloadUrl]?.downloadId
        ?: cachedDownloadIDs[downloadUrl] ?: null

    /**
     * 通过url获取下载任务状态
     */
    fun getDownloadTaskStatusByUrl(downloadUrl: String): SimpleDownloadTaskStatus? {
        return getDownloadTaskStatusByID(getDownloadTaskID(downloadUrl) ?: return null)
    }

    /**
     * 通过id获取下载任务状态
     */
    @SuppressLint("Range")
    fun getDownloadTaskStatusByID(id: Long): SimpleDownloadTaskStatus? {
        val cursor = downloadManager.query(DownloadManager.Query().apply {
            setFilterById(id)
        })
        if (cursor.moveToFirst()) {
            val status = SimpleDownloadTaskStatus(
                id = id,
                state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)),
                reason = cursor.getInt(cursor.getColumnIndex(COLUMN_REASON)),
                soFarBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                totalSizeBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            )
            cursor.close()
            return status
        }
        return null
    }

    /**
     * 通过url检查下载任务是否存在
     */
    fun checkDownloadTaskHasExistsByUrl(downloadUrl: String): Boolean {
        return checkDownloadTaskHasExistsByID(getDownloadTaskID(downloadUrl) ?: return false)
    }

    /**
     * 通过id检查下载任务是否存在
     */
    fun checkDownloadTaskHasExistsByID(id: Long): Boolean = getDownloadTaskStatusByID(id)?.hasExists
        ?: false

    /**
     * 开始下载任务
     */
    fun startDownloadTask(
        downloadTask: SimpleDownloadTask
    ) {
        // 防止重复提交下载任务
        if (!downloadTask.isAllowDuplicate && checkDownloadTaskHasExistsByUrl(downloadTask.downloadUrl)) {
            registerDownloadCompleteReceiver()
            return
        }
        // 添加到下载任务列表
        downloadTasks[downloadTask.downloadUrl] = downloadTask

        // 下载保存的文件夹和文件名
        val saveDir = downloadTask.downloadSaveDir
        val saveName = downloadTask.downloadSaveName
            ?: (System.currentTimeMillis().toString() + (downloadTask.downloadSaveNameSuffix
                ?: ""))
        // 设置下载路径
        val request = DownloadManager.Request(Uri.parse(downloadTask.downloadUrl))
        // 设置文件名和目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // android Q以上必须使用公共目录，否则需要调用文件框架供用户手动选择目录
            request.setDestinationInExternalPublicDir(saveDir, saveName)
        } else {
            // 如果android Q以下，我们则使用内部目录即可
            request.setDestinationInExternalFilesDir(context, saveDir, saveName)
        }
        // 配置wifi、移动网络均可下载
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
        )
        // 显示下载进度到通知栏
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        // 配置通知栏下载标题与描述
        request.setTitle(downloadTask.downloadTitle)
        request.setDescription(downloadTask.downloadDescription)
        // 启动下载任务
        downloadTask.downloadId = downloadManager.enqueue(request)
        addCachedDownloadID(downloadTask)
        // 发送准备下载事件
        sendDownloadStatusEvent(downloadTask, DownloadManager.STATUS_PENDING)
        // 注册广播接收者，监听下载状态
        registerDownloadCompleteReceiver()
    }

    // 根据id获取下载任务
    fun getDownloadTaskByID(id: Long): SimpleDownloadTask? {
        downloadTasks.values.forEach {
            if (it.downloadId == id) {
                return it
            }
        }
        return null
    }


    // 根据id获取下载好的文件uri，如果未下载好或未找到任务，则返回空
    fun getDownloadUriByID(id: Long): Uri? {
        val cursor = downloadManager.query(DownloadManager.Query().apply {
            setFilterById(id)
        })
        if (!cursor.moveToFirst()) return null
        val currentStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).let {
            if (it > 0) cursor.getInt(it) else null
        }
        var uri: Uri? = null
        if (currentStatus == DownloadManager.STATUS_SUCCESSFUL) {
            // android N以上需要接收content协议的Uri，而android N以下则需要file协议的Uri
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                downloadManager.getUriForDownloadedFile(id)
            } else {
                cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI).let {
                    if (it > 0) cursor.getString(it) else null
                }?.let { Uri.parse(it) }
            }
        }
        // 关闭游标
        cursor.close()
        return uri
    }


    // 发送下载完成事件
    fun sendDownloadStatusEvent(id: Long, status: Int) =
        sendDownloadStatusEvent(getDownloadTaskByID(id), status)

    // 发送下载完成事件
    fun sendDownloadStatusEvent(downloadTask: SimpleDownloadTask?, status: Int) =
        downloadTask?.run {
            downloadStatus = status
            EventBus.getDefault().post(this)
        }

    /**
     * 下载状态广播接收者
     */
    private var downloadCompleteReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) ?: return
            val cursor = downloadManager.query(DownloadManager.Query().apply {
                setFilterById(currentId)
            })
            if (!cursor.moveToFirst()) {
                return
            }
            when (val currentStatus =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING, DownloadManager.STATUS_RUNNING -> Unit
                DownloadManager.STATUS_SUCCESSFUL -> { // 下载成功
                    // android N以上需要接收content协议的Uri，而android N以下则需要file协议的Uri
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        downloadManager.getUriForDownloadedFile(currentId)
                    else Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    // 关闭游标
                    cursor.close()
                    // 获取相应下载任务
                    val downloadTask = getDownloadTaskByID(currentId)
                    // 自动安装
                    if (downloadTask?.isAutoOpen == true) {
                        if (DOWNLOAD_SAVE_NAME_SUFFIX_APK.equals(
                                downloadTask.downloadSaveNameSuffix,
                                true
                            )
                        ) {
                            // 安装APK
                            onInstallApk(context, uri)
                        }
                    }
                    // 解除广播接收
                    context?.unregisterReceiver(this)
                    // 发送下载完成事件
                    sendDownloadStatusEvent(downloadTask, currentStatus)
                }
                DownloadManager.STATUS_FAILED -> { // 下载错误
                    // 关闭游标
                    cursor.close();
                    // 获取相应下载任务
                    val downloadTask = getDownloadTaskByID(currentId)
                    // 弹出错误提示
                    downloadTask?.downloadFailedTips?.run {
                        Toast.makeText(
                            context,
                            this,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    // 解除广播接收
                    context?.unregisterReceiver(this)
                    // 发送下载完成事件
                    sendDownloadStatusEvent(downloadTask, currentStatus)
                }
            }
        }
    }

    // 注册广播接收
    fun registerDownloadCompleteReceiver() {
        context.registerReceiver(
            downloadCompleteReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    // 解除广播接收
    fun unregisterDownloadCompleteReceiver() = try {
        context?.unregisterReceiver(downloadCompleteReceiver)
    } catch (e: Exception) {
    }

    /**
     * 安装APK
     */
    fun onInstallApk(context: Context?, apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW);
        val type = "application/vnd.android.package-archive"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // android N以上在设置安装Uri时必须获取Uri权限，否则无法正常安装
//        var apkUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), apkFile)
            intent.setDataAndType(apkUri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // anddroid N以下可以直接设置安装Uri
            intent.setDataAndType(apkUri, type)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    fun openBrowserDownload(downloadApkUrl: String) =
        context.openBrowser(downloadApkUrl)
}


/**
 * 简单下载服务（会通过系统服务调用系统自带的下载器进行下载，适用于简单下载场景）
 */
class SimpleDownloadService : Service() {
    private val downloadUtil: SimpleDownloadUtil by lazy {
        return@lazy SimpleDownloadUtil(SampleApp.instance)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        run startDownloadApk@{
            var downloadTask =
                intent?.getSerializableExtra(dataNameFordownloadTask) as? SimpleDownloadTask
                    ?: return@startDownloadApk
            downloadUtil.startDownloadTask(downloadTask)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        downloadUtil.unregisterDownloadCompleteReceiver()
        super.onDestroy()
    }

    companion object {
        private const val dataNameFordownloadTask: String = "dataNameFordownloadTask"
        fun startService(
            context: Context?,
            downloadTask: SimpleDownloadTask
        ) {
            context?.startService(Intent(context, SimpleDownloadService::class.java).apply {
                putExtra(dataNameFordownloadTask, downloadTask)
            })
        }
    }
}