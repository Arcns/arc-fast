package com.arc.fast.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.arc.fast.core.R
import java.io.File


/**
 * 隐藏键盘
 */
fun Fragment.hideSoftInputFromWindow() = activity?.hideSoftInputFromWindow()

/**
 * 隐藏键盘
 */
fun Activity.hideSoftInputFromWindow() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

/**
 * 显示键盘
 */
fun EditText.showSoftInput() = this.run {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.showSoftInput(
        this,
        0
    );
}

/**
 * 设置列表滚动时自动隐藏软键盘
 */
fun Fragment.setupAutoHideSoftInput(
    rvView: RecyclerView,
    lifecycleOwner: LifecycleOwner = viewLifecycleOwner
) = requireActivity().setupAutoHideSoftInput(rvView, lifecycleOwner)

/**
 * 设置列表滚动时自动隐藏软键盘
 */
fun FragmentActivity.setupAutoHideSoftInput(
    rvView: RecyclerView,
    lifecycleOwner: LifecycleOwner = this
) {
    rvView.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) hideSoftInputFromWindow()
    }
    rvView.isFocusable = false
    rvView.isFocusableInTouchMode = false
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            lifecycleOwner.lifecycle.removeObserver(this)
            rvView.onFocusChangeListener = null
            super.onDestroy(owner)
        }
    })
}

/**
 * 通过包名检查是否有安装APP
 */
fun Context.checkHasInstallAppByPackageName(packageName: String): Boolean =
    packageManager.getInstalledPackages(0).firstOrNull { it.packageName == packageName } != null

/**
 * 回到桌面
 */
fun Activity.backToLauncher() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivity(intent)
}

/**
 * 回到桌面
 */
fun Fragment.backToLauncher() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivity(intent)
}

/**
 * 是否拥有悬浮窗权限
 */
val Context.canDrawOverlays: Boolean
    get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) Settings.canDrawOverlays(this)
    else try {
        val mgr = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val viewToAdd = View(this)
        val params = WindowManager.LayoutParams(
            0,
            0,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
        viewToAdd.layoutParams = params
        mgr.addView(viewToAdd, params)
        mgr.removeView(viewToAdd)
        true
    } catch (ignore: Exception) {
        false
    }


/**
 * 打开悬浮窗设置页面
 */
fun Activity.goDrawOverlaysSettings(onFailure: ((e: Exception) -> Unit)? = null) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0以上
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent, 100)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0-8.0
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 100)
        }
    } catch (e: Exception) {
        onFailure?.invoke(e)
    }
}


/**
 * 重启APP
 */
fun Activity.restartApp(isFinish: Boolean = true) {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val mainIntent = Intent.makeRestartActivityTask(intent?.component)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
    if (isFinish) finish()
}

/**
 * 重启APP
 */
fun Fragment.restartApp(isFinish: Boolean = true) = activity?.restartApp(isFinish)


fun Context.openAppByPath(
    path: String,
    mimeType: String = path.mimeType,
    authority: String? = null
) {
    if (path.isInternetResources) {
        openAppByUri(Uri.parse(path), mimeType)
    } else if (authority != null) {
        openAppByFile(File(path), mimeType, authority)
    }
}

fun Context.openAppByUri(uri: Uri, mimeType: String) {
    var intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, mimeType)
    if (intent.resolveActivity(packageManager) == null) {
        Toast.makeText(
            this,
            R.string.text_not_find_open_app_by_mime_type.string,
            Toast.LENGTH_LONG
        )
            .show()
        return
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openAppByFile(
    file: File,
    mimeType: String = file.absolutePath.mimeType,
    authority: String
) {
    if (!file.exists()) {
        return
    }
    var intent = Intent(Intent.ACTION_VIEW)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // android N以上必须获取Uri权限，否则无法正常使用
        var uri = FileProvider.getUriForFile(this, authority, file)
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    } else {
        // anddroid N以下可以直接设置Uri
        var uri = file.toUri()
        intent.setDataAndType(uri, mimeType)
    }
    if (intent.resolveActivity(packageManager) == null) {
        Toast.makeText(
            this,
            R.string.text_not_find_open_app_by_mime_type.string,
            Toast.LENGTH_LONG
        )
            .show()
        return
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openBrowser(url: String): Boolean {
    try {
        if (url.isInternetResources) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            return true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}