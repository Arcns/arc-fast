package com.arc.fast.sample

import android.app.DownloadManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.arc.fast.sample.data.LocalData
import com.arc.fast.sample.data.entity.ApkInfo
import com.arc.fast.sample.data.entity.AppUpdate
import com.arc.fast.sample.databinding.ActivityContainerBinding
import com.arc.fast.sample.extension.applyFullScreen
import com.arc.fast.sample.extension.setLightSystemBar
import com.arc.fast.sample.utils.SimpleDownloadService
import com.arc.fast.sample.utils.SimpleDownloadTask
import com.arc.fast.sample.utils.SimpleDownloadUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 容器Activity
 */
class ContainerActivity : AppCompatActivity() {

    lateinit var binding: ActivityContainerBinding
    val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFullScreen()
        setLightSystemBar(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_container)
        EventBus.getDefault().register(this)
        // Toast
        appViewModel.eventToast.flowWithLifecycle(lifecycle).onEach {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }.launchIn(lifecycleScope)
        // 弹窗
        appViewModel.eventDialog.flowWithLifecycle(lifecycle).onEach {
            MaterialAlertDialogBuilder(this)
                .setTitle(it.title)
                .setMessage(it.message)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
        }.launchIn(lifecycleScope)
        // app更新
        appViewModel.eventAppUpdate.flowWithLifecycle(lifecycle).onEach {
            checkAppUpdate(it)
        }.launchIn(lifecycleScope)
    }

    private fun checkAppUpdate(value: AppUpdate) {
        // 检查是否已下载完成
        val downloadedApkInfo = LocalData.downloadedApkInfo
        if (downloadedApkInfo?.url == value.apk_url && downloadedApkInfo?.version_code == value.version_code) {
            if (onAppInstall(downloadedApkInfo)) {
                return
            }
        }
        // 询问用户是否更新
        MaterialAlertDialogBuilder(this)
            .setTitle(value.title)
            .setMessage(value.content)
            .setPositiveButton(R.string.update) { _, _ ->
                onAppUpdate(value)
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    private fun onAppUpdate(value: AppUpdate) {
        // 使用谷歌商店下载
//        val googlePlayBackage = "com.android.vending"
//        if (packageManager.getLaunchIntentForPackage(googlePlayBackage) != null) {
//            startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
//                ).setPackage(googlePlayBackage)
//            )
//            return
//        }
        // 启动下载任务
        appViewModel.showToast(getString(R.string.downloading))
        SimpleDownloadService.startService(
            this,
            SimpleDownloadTask(
                downloadUrl = value.apk_url!!,
                downloadTitle = getString(
                    R.string.app_name
                ),
                downloadDescription = getString(R.string.downloading),
                downloadSaveNameSuffix = ".apk",
                isAutoOpen = false,
                extraLongData = value.version_code,
                extraStringData = value.content
            )
        )
    }


    private fun onAppInstall(apkInfo: ApkInfo): Boolean {
        val simpleDownloadUtil = SimpleDownloadUtil(this)
        val uri = simpleDownloadUtil.getDownloadUriByID(apkInfo.downloadId)
        return if (uri != null) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_update_downloaded)
                .setMessage(R.string.app_update_downloaded_tip)
                .setPositiveButton(R.string.install) { _, _ ->

                    simpleDownloadUtil.onInstallApk(this, uri)
                }.setNegativeButton(R.string.cancel) { _, _ -> }
                .setCancelable(false)
                .show()
            true
        } else {
            false
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppDownloadStatusChange(downloadTask: SimpleDownloadTask) {
        when (downloadTask.downloadStatus) {
            // 缓存下载信息
            DownloadManager.STATUS_PENDING, DownloadManager.STATUS_SUCCESSFUL -> {
                val apkInfo = ApkInfo(
                    url = downloadTask.downloadUrl,
                    version_code = downloadTask.extraLongData ?: return,
                    downloadId = downloadTask.downloadId ?: return,
                    update_content = downloadTask.extraStringData
                )
                LocalData.downloadedApkInfo = apkInfo
                if (downloadTask.downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    onAppInstall(apkInfo)
                }
            }
            DownloadManager.STATUS_FAILED -> {
                LocalData.downloadedApkInfo = null
            }
        }
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}