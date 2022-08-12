package com.arc.fast.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.arcns.core.APP
import com.arcns.core.file.getRandomPhotoCacheFilePath
import com.arcns.core.file.mimeType
import com.arcns.core.file.tryClose
import com.arcns.xfile.FileUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import ezy.assist.compat.SettingsCompat
import me.shouheng.compress.Compress
import me.shouheng.compress.strategy.Strategies
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt


/**
 * 获取当前APP的版本名
 */
val Context.versionName: String
    get() = packageManager.getPackageInfo(
        packageName, 0
    ).versionName

/**
 * 获取当前APP的版本号
 */
val Context.versionCode: Long
    get() = PackageInfoCompat.getLongVersionCode(
        packageManager.getPackageInfo(
            packageName,
            0
        )
    )

/**
 * 获取屏幕大小（会自动减去窗口装饰）
 */
val Context.screenSize: Size
    get() = resources.displayMetrics.let {
        Size(
            it.widthPixels,
            it.heightPixels
        )
    }

/**
 * 获取屏幕宽度（会自动减去窗口装饰）
 */
val Context.screenWidth: Int get() = screenSize.width

/**
 * 获取屏幕高度（会自动减去窗口装饰）
 */
val Context.screenHeight: Int get() = screenSize.height

/**
 * 获取真实的屏幕大小（不会减去窗口装饰）
 */
val Context.realScreenSize: Size
    get() {
        val windowManager = getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.let {
                Size(it.width(), it.height())
            }
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }


/**
 * 获取真实的屏幕宽度（不会减去窗口装饰）
 */
val Context.realScreenWidth: Int get() = realScreenSize.width

/**
 * 获取真实的屏幕高度（不会减去窗口装饰）
 */
val Context.realScreenHeight: Int get() = realScreenSize.height


/***********************************格式转换**************************************/

// string格式转datetime
fun String.asDateTime(format: String?): Date? = try {
    SimpleDateFormat(format).parse(this)
} catch (e: java.lang.Exception) {
    null
}


/***********************************Gson**************************************/
/
/***********************************键盘显示隐藏**************************************/


/***********************************系统状态栏操作**************************************/

/**
 * 设置透明状态栏
 */
fun Activity.setupTransparentStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = Color.TRANSPARENT
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

// 获取系统状态栏高度
fun Context.getStatusBarHeight(): Int {
    var result = 0;
    var resId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resId > 0) {
        result = resources.getDimensionPixelSize(resId);
    }
    return result
}

// 获取系统导航栏高度
fun Context.getNavigationBarHeight(): Int {
    var result = 0;
    var resId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resId > 0) {
        result = resources.getDimensionPixelSize(resId);
    }
    return result
}

/**
 * 获取ActionBar默认高度
 */
fun Context?.getActionBarHeight(): Int {
    if (this == null) {
        return 48.dp
    }
    val typedValue = TypedValue()
    if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
        return TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
    return 48.dp
}

/**
 * 上内边距增加系统状态栏的高度大小
 */
fun View.setPaddingStatusBarHeight(
    notRepeat: Boolean = false, // 判断是否重复添加，若现有padding高度为状态栏高度，则不再添加
    autoHeightExpansion: Boolean = true // 是否自动扩展高度，增加状态栏高度（仅当非自适应时生效）
) {
    val statusBarHeight = context.getStatusBarHeight()
    if (notRepeat && paddingTop == statusBarHeight) {
        // 已添加状态栏高度时不再重复添加
        return
    }
    if (autoHeightExpansion && layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
        // 如果高度不是自适应时，需要增加状态栏的高度
        layoutParams = layoutParams.apply {
            height += statusBarHeight
        }
    }
    setPadding(
        paddingLeft,
        paddingTop + statusBarHeight,
        paddingRight,
        paddingBottom
    )
}

/**
 * 上外边距增加系统状态栏的高度大小
 */
fun View.setMarginStatusBarHeight() {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(
                leftMargin,
                topMargin + context.getStatusBarHeight(),
                rightMargin,
                bottomMargin
            )
        }
    }
}

/**
 * 设置状态栏文字高亮
 */

fun Fragment.setLightSystemStatusBarText() = activity?.setLightSystemStatusBarText()

/**
 * 设置状态栏文字高亮
 */
fun Activity.setLightSystemStatusBarText() {
    window?.decorView?.systemUiVisibility = window?.decorView?.systemUiVisibility?.let {
        it or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } ?: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

/**
 * 清除状态栏文字高亮
 */
fun Fragment.clearLightSystemStatusBarText() = activity?.clearLightSystemStatusBarText()

/**
 * 清除状态栏文字高亮
 */
fun Activity.clearLightSystemStatusBarText() {
    window?.decorView?.systemUiVisibility?.run {
        window?.decorView?.systemUiVisibility =
            this and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // 与非运算，同java的&~
    }
}


/***********************************Uri操作**************************************/

fun File.conversionUri(authority: String = APP.fileProviderAuthority!!): Uri? = try {
    if (!exists()) null
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(APP.INSTANCE, authority, this);
    } else {
        Uri.fromFile(this);
    }
} catch (e: Exception) {
    e.printStackTrace()
    null
}


/***********************************图片显示、保存与压缩**************************************/

/**
 * 获取bitmap大小
 */
fun String.getBitmapSize(): WidthHeight? = File(this).getBitmapSize()


/**
 * 获取bitmap大小
 */
fun File.getBitmapSize(): WidthHeight? {
    if (!exists()) return null
    return FileInputStream(this).getBitmapSize()
}

/**
 * 获取bitmap大小
 */
fun Uri.getBitmapSize(): WidthHeight? {
    return APP.INSTANCE.contentResolver.openInputStream(this)?.getBitmapSize()
}

/**
 * 获取bitmap大小
 */
fun Int.getBitmapSize(): WidthHeight? = try {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(APP.INSTANCE.resources, this, options)
    WidthHeight(
        width = options.outWidth,
        height = options.outHeight
    )
} catch (e: java.lang.Exception) {
    null
}

/**
 * 获取bitmap大小
 */
fun InputStream.getBitmapSize(): WidthHeight? = try {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(this, null, options)
    WidthHeight(
        width = options.outWidth,
        height = options.outHeight
    )
} catch (e: java.lang.Exception) {
    null
} finally {
    tryClose()
}

/**
 * 计算缩放后的bitmap大小
 */
fun String.calculateBitmapScaledSize(width: Int? = null, height: Int? = null): ScaledWidthHeight? =
    getBitmapSize()?.calculateBitmapScaledSize(width, height)

/**
 * 计算缩放后的bitmap大小
 */
fun File.calculateBitmapScaledSize(
    width: Int? = null,
    height: Int? = null
): ScaledWidthHeight? = getBitmapSize()?.calculateBitmapScaledSize(width, height)

/**
 * 计算缩放后的bitmap大小
 */
fun Uri.calculateBitmapScaledSize(
    width: Int? = null,
    height: Int? = null
): ScaledWidthHeight? = getBitmapSize()?.calculateBitmapScaledSize(width, height)

/**
 * 计算缩放后的bitmap大小
 */
fun Int.calculateBitmapScaledSize(
    width: Int? = null,
    height: Int? = null
): ScaledWidthHeight? = getBitmapSize()?.calculateBitmapScaledSize(width, height)

/**
 * 计算缩放后的bitmap大小
 */
fun InputStream.calculateBitmapScaledSize(
    width: Int? = null,
    height: Int? = null
): ScaledWidthHeight? = getBitmapSize()?.calculateBitmapScaledSize(width, height)

/**
 * 计算缩放后的bitmap大小
 */
fun WidthHeight.calculateBitmapScaledSize(
    width: Int? = null,
    height: Int? = null
): ScaledWidthHeight? {
    val size = this
    var newWidth = if (width == 0) null else width
    var newHeight = if (height == 0) null else height
    if (newWidth == null && newHeight == null) {
        return ScaledWidthHeight(
            width = size.width,
            height = size.height,
            newWidth = size.width,
            newHeight = size.height
        )
    }
    if (newWidth == null) {
        var scale = newHeight!!.toDouble() / size.height
        newWidth = (size.width * scale).toInt()
    }
    if (newHeight == null) {
        var scale = newWidth!!.toDouble() / size.width
        newHeight = (size.height * scale).toInt()
    }
    return ScaledWidthHeight(
        width = size.width,
        height = size.height,
        newWidth = newWidth,
        newHeight = newHeight
    )
}

// 把文件路径转换为bitmap，并设置大小
fun String.bitmap(width: Int? = null, height: Int? = null): Bitmap? =
    File(this).bitmap(width, height)

// 把文件转换为bitmap，并设置大小
fun File.bitmap(width: Int? = null, height: Int? = null): Bitmap? =
    if (!exists()) null
    else FileInputStream(this).bitmap(
        if (width == null && height == null) null
        else calculateBitmapScaledSize(width, height)
    )

// 把Uri文件转换为bitmap，并设置大小
fun Uri.bitmap(width: Int? = null, height: Int? = null): Bitmap? =
    APP.INSTANCE.contentResolver.openInputStream(this)?.bitmap(
        if (width == null && height == null) null
        else calculateBitmapScaledSize(width, height)
    )

/**
 * 把文件流转换为bitmap，并设置大小
 */
fun InputStream.bitmap(size: ScaledWidthHeight? = null): Bitmap? {
    try {
        if (size == null) return BitmapFactory.decodeStream(this)
        // 计算图片缩放比例
        val minSideLength = size.newWidth.coerceAtMost(size.newHeight)
        val options = BitmapFactory.Options()
        options.inSampleSize = computeBitmapSampleSize(
            options, minSideLength,
            size.newWidth * size.newHeight
        )
        options.inInputShareable = true;
        options.inPurgeable = true;
        return BitmapFactory.decodeStream(this, null, options)
    } catch (e: java.lang.Exception) {
        return null
    } finally {
        tryClose()
    }
}

/**
 * 把资源转换为bitmap，并设置大小
 */
fun Int.bitmap(width: Int? = null, height: Int? = null): Bitmap? {
    try {
        if (width == null && height == null) return BitmapFactory.decodeResource(
            APP.INSTANCE.resources,
            this
        )
        // 计算缩放后的bitmap大小
        val size = calculateBitmapScaledSize(width, height) ?: return null
        // 计算图片缩放比例
        val minSideLength = size.newWidth.coerceAtMost(size.newHeight)
        val options = BitmapFactory.Options()
        options.inSampleSize = computeBitmapSampleSize(
            options, minSideLength,
            size.newWidth * size.newHeight
        )
        options.inInputShareable = true;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(APP.INSTANCE.resources, this, options)
    } catch (e: java.lang.Exception) {
        return null
    }
}

private fun computeBitmapSampleSize(
    options: BitmapFactory.Options?,
    minSideLength: Int, maxNumOfPixels: Int
): Int {
    val initialSize = computeBitmapInitialSampleSize(
        options!!, minSideLength,
        maxNumOfPixels
    )
    var roundedSize: Int
    if (initialSize <= 8) {
        roundedSize = 1
        while (roundedSize < initialSize) {
            roundedSize = roundedSize shl 1
        }
    } else {
        roundedSize = (initialSize + 7) / 8 * 8
    }
    return roundedSize
}

private fun computeBitmapInitialSampleSize(
    options: BitmapFactory.Options,
    minSideLength: Int, maxNumOfPixels: Int
): Int {
    val w = options.outWidth.toDouble()
    val h = options.outHeight.toDouble()
    val lowerBound = if (maxNumOfPixels == -1) 1 else ceil(
        sqrt(w * h / maxNumOfPixels)
    ).toInt()
    val upperBound =
        if (minSideLength == -1) 128 else floor(w / minSideLength).coerceAtMost(floor(h / minSideLength))
            .toInt()
    if (upperBound < lowerBound) {
        // return the larger one when there is no overlapping zone.
        return lowerBound
    }
    return if (maxNumOfPixels == -1 && minSideLength == -1) {
        1
    } else if (minSideLength == -1) {
        lowerBound
    } else {
        upperBound
    }
}


// 把图片资源转换为bitmap，并设置大小
fun Int.bitmap(context: Context?, newWidth: Int? = null, newHeight: Int? = null): Bitmap {
    return BitmapFactory.decodeResource(context?.resources, this).zoomImg(newWidth, newHeight)
}

// 设置bitmap大小
fun Bitmap.zoomImg(newWidth: Int? = null, newHeight: Int? = null): Bitmap { //获得图片的宽高
    if (newWidth == null && newHeight == null) {
        return this;
    }
    var scaleWidth: Float = 1f;
    var scaleHeight: Float = 1f;
    newHeight?.apply {
        scaleHeight = newHeight.toFloat() / height
        if (newWidth == null) {
            scaleWidth = scaleHeight
        }
    }
    newWidth?.apply {
        scaleWidth = newWidth.toFloat() / width
        if (newHeight == null) {
            scaleHeight = scaleWidth
        }
    }
    //取得想要缩放的matrix参数
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    //得到新的图片
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * bitmap转base64
 */
fun Bitmap.asBase64(): String? {
    var result: String? = null
    var byteArrayOutputStream: ByteArrayOutputStream? = null
    try {
        byteArrayOutputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        byteArrayOutputStream.flush()
        byteArrayOutputStream.close()
        result = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            byteArrayOutputStream?.flush()
            byteArrayOutputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return result
}

/**
 * 文件路径转base64
 */
fun String.asBase64(): String? {
    var file = File(this)
    if (!file.exists()) {
        return null
    }
    return file.asBase64()
}

/**
 * 文件转base64
 */
fun File.asBase64(): String? {
    var result: String? = null
    if (!exists()) {
        return null
    }
    var inputStream: FileInputStream? = null
    try {
        inputStream = FileInputStream(this)
        var bytes = ByteArray(inputStream.available())
        var length = inputStream.read(bytes)
        result = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT)
    } catch (e: java.lang.Exception) {
        e.printStackTrace();
    } finally {
        try {
            inputStream?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace();
        }
    }
    return result
}

/**
 * 通过Picasso设置图片
 */
fun ImageView.setImageViaPicasso(
    image: Any?,
    placeholderDrawable: Drawable? = null,
    errorDrawable: Drawable? = null,
    size: Float? = null,
    w: Float? = null,
    h: Float? = null,
    centerInside: Boolean? = null, //默认false
    cache: Boolean? = null, // 默认true
    noFade: Boolean? = null, //默认为false
    asBackground: Boolean? = null, //默认为false
    highQualityBitmap: Boolean? = null //高质量bitmap，默认为false
) {

    if (image == null) {
        if (asBackground == true) {
            this.background = null
        } else {
            this.setImageDrawable(null)
        }
        return
    }
    val picasso = Picasso.get().apply {
        isLoggingEnabled = true
    }
    var requestCreator = when (image) {
        is Int -> picasso.load(image)
        is String -> {
            if (image.isInternetResources)
                picasso.load(image)
            else
                picasso.load(File(image))
        }
        is Uri -> picasso.load(image)
        is File -> picasso.load(image)
        else -> return
    }
    // 设置缓存机制
    if (cache == false) {
        requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
    }
    // 设置图片质量
    requestCreator.config(if (highQualityBitmap == true) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
    // 设置加载中的图片
    placeholderDrawable?.let {
        requestCreator.placeholder(it)
    }
    // 设置加载错误的图片
    errorDrawable?.let {
        requestCreator.error(it)
    }
    // 设置图片大小
    val width = w ?: size ?: 0f
    val height = h ?: size ?: 0f
    if (width != 0f || height != 0f) {
        requestCreator.resize(width.toInt(), height.toInt())
        requestCreator.onlyScaleDown()
        if (centerInside == true) {
            // 自适应全部显示
            requestCreator.centerInside()
        } else {
            // 自适应填充满
            requestCreator.centerCrop()
        }
    }
    // 是否显示动画
    if (noFade == true) {
        requestCreator.noFade()
    }
    if (asBackground == true) {
        // 设置为背景
        requestCreator.into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                background = BitmapDrawable(context.resources, bitmap)
            }
        })
    } else {
        // 设置为src
        requestCreator.into(this)
    }
}

/**
 * 通过Glide设置图片
 */
fun ImageView.setImageViaGlide(
    image: Any?,
    placeholderDrawable: Drawable? = null,
    errorDrawable: Drawable? = null,
    size: Float? = null,
    w: Float? = null,
    h: Float? = null,
    centerInside: Boolean? = null, //默认false
    cache: Boolean? = null, // 默认true
    noFade: Boolean? = null, //默认为false
    asBackground: Boolean? = null, //默认为false
    highQualityBitmap: Boolean? = null, //高质量bitmap，默认为false
    asGif: Boolean? = null //是否为gif，默认为false
) {
    if (image == null) {
        if (asBackground == true) {
            this.background = null
        } else {
            this.setImageDrawable(null)
        }
        return
    }
    val glide =
        Glide.with(this)
    var requestBuilder: RequestBuilder<*> = when (image) {
        is Int -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        is String -> {
            val checkAsGif = asGif ?: image.endsWith(".gif", true)
            if (image.isInternetResources) {
                if (checkAsGif) glide.asGif().load(image) else glide.load(image)
            } else {
                if (checkAsGif) glide.asGif().load(File(image)) else glide.load(File(image))
            }
        }
        is Uri -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        is File -> {
            val checkAsGif = asGif ?: image.absolutePath.endsWith(".gif", true)
            if (checkAsGif == true) glide.asGif().load(image) else glide.load(image)
        }
        is GlideUrl -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        else -> return
    }
//    requestBuilder.priority(Priority.LOW)
//    requestBuilder =requestBuilder.thumbnail(0.1f)
    // 设置缓存机制
    if (cache == false) {
        requestBuilder =
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
    }
    // 设置图片质量
    requestBuilder =
        requestBuilder.format(if (highQualityBitmap == true) DecodeFormat.PREFER_ARGB_8888 else DecodeFormat.PREFER_RGB_565)
    // 设置加载中的图片
    placeholderDrawable?.let {
        requestBuilder = requestBuilder.placeholder(it)
    }
    // 设置加载错误的图片
    errorDrawable?.let {
        requestBuilder = requestBuilder.error(it)
    }
    // 设置图片大小
    val width = w ?: size ?: 0f
    val height = h ?: size ?: 0f
    if (width != 0f || height != 0f) {
        requestBuilder = requestBuilder.override(width.toInt(), height.toInt())
        requestBuilder = if (centerInside == true) {
            // 自适应全部显示
            requestBuilder.centerInside()
        } else {
            // 自适应填充满
            requestBuilder.centerCrop()
        }
    }
    // 是否显示动画
    if (noFade == true) {
        requestBuilder = requestBuilder.dontAnimate()
    }
    if (asBackground == true) {
        // 设置为背景
        if (asGif == true)
            (requestBuilder as? RequestBuilder<GifDrawable>)?.into(object :
                CustomTarget<GifDrawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    this@setImageViaGlide.background = resource
                }

            })
        else
            (requestBuilder as? RequestBuilder<Drawable>)?.into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    this@setImageViaGlide.background = resource
                }
            })
    } else {
        requestBuilder.into(this)
    }
}


/**
 * 通过Glide设置图片
 */
fun Any.loadDrawable(
    image: Any?, // 图片资源（Int、String、Uri、File、GlideUrl）
    cache: Boolean? = null, // 默认true
    highQualityBitmap: Boolean? = null, //高质量bitmap，默认为false
    asGif: Boolean? = null, //是否为gif，默认为false
    onFailed: ((Exception?) -> Unit)? = null,
    onSuccess: (Drawable) -> Unit
) {

    val glide = when (this) {
        is View -> Glide.with(this)
        is Context -> Glide.with(this)
        else -> {
            onFailed?.invoke(java.lang.Exception("Need to be accessed through view or context"))
            return
        }
    }
    var requestBuilder: RequestBuilder<*> = when (image) {
        is Int -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        is String -> {
            val checkAsGif = asGif ?: image.endsWith(".gif", true)
            if (image.isInternetResources) {
                if (checkAsGif) glide.asGif().load(image) else glide.load(image)
            } else {
                if (checkAsGif) glide.asGif().load(File(image)) else glide.load(File(image))
            }
        }
        is Uri -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        is File -> {
            val checkAsGif = asGif ?: image.absolutePath.endsWith(".gif", true)
            if (checkAsGif == true) glide.asGif().load(image) else glide.load(image)
        }
        is GlideUrl -> if (asGif == true) glide.asGif().load(image) else glide.load(image)
        else -> return
    }
    // 设置缓存机制
    if (cache == false) {
        requestBuilder =
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
    }
    // 设置图片质量
    requestBuilder =
        requestBuilder.format(if (highQualityBitmap == true) DecodeFormat.PREFER_ARGB_8888 else DecodeFormat.PREFER_RGB_565)
    if (asGif == true)
        (requestBuilder as? RequestBuilder<GifDrawable>)?.into(object :
            CustomTarget<GifDrawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                onFailed?.invoke(null)
            }

            override fun onResourceReady(
                resource: GifDrawable,
                transition: Transition<in GifDrawable>?
            ) {
                onSuccess.invoke(resource)
            }

        })
    else
        (requestBuilder as? RequestBuilder<Drawable>)?.into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                onFailed?.invoke(null)
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                onSuccess.invoke(resource)
            }
        })
}

fun Int.saveImageAsLocal(
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = saveImageAsLocalOrNull(
    this,
    width,
    height,
    centerInside,
    highQualityBitmap,
    compressQuality,
    isOriginal,
    saveFilePath
)

fun String.saveImageAsLocal(
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = saveImageAsLocalOrNull(
    this,
    width,
    height,
    centerInside,
    highQualityBitmap,
    compressQuality,
    isOriginal,
    saveFilePath
)

fun File.saveImageAsLocal(
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = saveImageAsLocalOrNull(
    this,
    width,
    height,
    centerInside,
    highQualityBitmap,
    compressQuality,
    isOriginal,
    saveFilePath
)

fun Uri.saveImageAsLocal(
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = saveImageAsLocalOrNull(
    this,
    width,
    height,
    centerInside,
    highQualityBitmap,
    compressQuality,
    isOriginal,
    saveFilePath
)

fun Bitmap.saveImageAsLocal(
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = saveImageAsLocalOrNull(
    this,
    width,
    height,
    centerInside,
    highQualityBitmap,
    compressQuality,
    isOriginal,
    saveFilePath
)

fun saveImageAsLocalOrNull(
    image: Any?,
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File? = try {
    saveImageAsLocal(
        image,
        width,
        height,
        centerInside,
        highQualityBitmap,
        compressQuality,
        isOriginal,
        saveFilePath
    )
} catch (e: java.lang.Exception) {
    null
}

fun saveImageAsLocal(
    image: Any?,
    width: Float = 0f,
    height: Float = 0f,
    centerInside: Boolean = false,
    highQualityBitmap: Boolean = true,
    compressQuality: Int = 80,
    isOriginal: Boolean = false,
    saveFilePath: String? = null
): File {
    if (image == null) {
        throw java.lang.Exception("iamge is null")
    }
    val filePath = if (saveFilePath.isNullOrBlank()) getRandomPhotoCacheFilePath() else saveFilePath
    if (isOriginal || (compressQuality == 100 && highQualityBitmap)) {
        val bitmap = when (image) {
            is Bitmap -> image
            is Int -> image.bitmap()
            is String -> image.bitmap()
            is Uri -> image.bitmap()
            is File -> image.bitmap()
            else -> null
        } ?: throw java.lang.Exception("iamge as to bitmap error")
        val localFile = File(filePath)
        val localFileOutputStream = FileOutputStream(localFile)
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream)
            return localFile
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
            localFileOutputStream.tryClose()
        }
    }
    try {
        val newWidth: Int
        val newHeight: Int
        val bitmap: Bitmap? =
            if (image is Bitmap) {
                newWidth = image.width
                newHeight = image.height
                image
            } else {
                var requestBuilder =
                    Glide.with(APP.INSTANCE).asBitmap()
                when (image) {
                    is Int -> requestBuilder.load(image)
                    is String -> if (image.isInternetResources) requestBuilder.load(image) else requestBuilder.load(
                        File(image)
                    )
                    is Uri -> requestBuilder.load(image)
                    is File -> requestBuilder.load(image)
                    else -> throw java.lang.Exception("iamge type error")
                }
                if (width != 0f && height != 0f) {
                    newWidth = width.toInt()
                    newHeight = height.toInt()
                } else {
                    val size = when (image) {
                        is Int -> image.calculateBitmapScaledSize(width.toInt(), height.toInt())
                        is String -> image.calculateBitmapScaledSize(width.toInt(), height.toInt())
                        is Uri -> image.calculateBitmapScaledSize(width.toInt(), height.toInt())
                        is File -> image.calculateBitmapScaledSize(width.toInt(), height.toInt())
                        else -> null
                    } ?: throw java.lang.Exception("iamge calculate scaled size error")
                    newWidth = size.newWidth
                    newHeight = size.newHeight
                }
                requestBuilder = requestBuilder.override(newWidth, newHeight)
                requestBuilder = if (centerInside) {
                    requestBuilder.centerInside()
                } else {
                    requestBuilder.centerCrop()
                }
                requestBuilder =
                    requestBuilder.format(if (highQualityBitmap) DecodeFormat.PREFER_ARGB_8888 else DecodeFormat.PREFER_RGB_565)
                // 让Glide在当前线程同步加载
                requestBuilder.submit().get()
            }
        // 开始压缩
        return Compress.with(APP.INSTANCE, bitmap)
            .setQuality(compressQuality)
            .setTargetDir(FileUtil.getFileDirectory(filePath))
            .setCacheNameFactory {
                FileUtil.getFileName(filePath)
            }
            .strategy(Strategies.compressor())
            .setMaxWidth(newWidth.toFloat())
            .setMaxHeight(newHeight.toFloat())
            .get()
    } catch (e: java.lang.Exception) {
        throw e
    }
}

// 旋转bitmap
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

// yuv420sp视频帧转bitmap
fun ByteArray.yuv420spToBitmap(width: Int, height: Int): Bitmap {
    val image = YuvImage(this, ImageFormat.NV21, width, height, null)
    val stream = ByteArrayOutputStream()
    image.compressToJpeg(Rect(0, 0, width, height), 100, stream)
    val bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
    stream.tryClose()
    return bitmap
}

/***********************************打开app**************************************/


/***********************************app操作**************************************/

