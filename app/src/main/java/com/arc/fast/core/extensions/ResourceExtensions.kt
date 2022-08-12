package com.arc.fast.core.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import com.arc.fast.core.FastCore
import com.arc.fast.core.util.invokeOrNull
import kotlin.math.roundToInt


/**
 * 获取string资源
 */
val Int.string: String get() = FastCore.context.getString(this)

/**
 * 获取string资源
 */
val Int.stringOrNull: String? get() = invokeOrNull { string }


/**
 * 获取string资源
 */
fun Int.string(vararg values: Any?): String = FastCore.context.getString(this, *values)


/**
 * 获取string资源
 */
fun Int.stringOrNull(vararg values: Any?): String? = invokeOrNull { string(*values) }

/**
 * 获取drawable资源
 */
val Int.drawable: Drawable get() = ContextCompat.getDrawable(FastCore.context, this)!!

/**
 * 获取drawable资源
 */
val Int.drawableOrNull: Drawable?
    get() = invokeOrNull {
        ContextCompat.getDrawable(
            FastCore.context,
            this
        )
    }

/**
 * 获取color资源
 */
val Int.color: Int get() = ContextCompat.getColor(FastCore.context, this)

/**
 * 获取color资源
 */
val Int.colorOrNull: Int? get() = invokeOrNull { color }


/**
 * hex color 转 color
 */
val String.color: Int get() = Color.parseColor(if (this.startsWith("#")) this else "#$this")

/**
 * hex color 转 color
 */
val String.colorOrNull: Int? get() = invokeOrNull { color }

/**
 * color 转 hex color
 */
val Int.hexColor: String get() = "#${Integer.toHexString(this)}"

/**
 * color 转 hex color
 */
val Int.hexColorOrNull: String? get() = invokeOrNull { hexColor }


/**
 * 判断color是否为亮色调
 */
val Int.isLightColor get() = ColorUtils.calculateLuminance(this) >= 0.5

/**
 * color亮色度
 */
val Int.lightColorNess get() = ColorUtils.calculateLuminance(this)

/**
 * 获取dimen资源
 */
val Int.dimen: Float get() = FastCore.context.resources.getDimension(this)

/**
 * 获取dimen资源
 */
val Int.dimenOrNull: Float? get() = invokeOrNull { dimen }


/**
 * 获取attr资源
 */
fun Context.getAttributeResource(attr: Int, defResId: Int? = null): Int? = with(TypedValue()) {
    val hasValue = theme.resolveAttribute(attr, this, true)
    if (hasValue) resourceId else defResId
}

/**
 * 获取attr资源 selectableItemBackground
 */
val Context.selectableItemBackgroundRes: Int? get() = getAttributeResource(android.R.attr.selectableItemBackground)

/**
 * 获取attr资源 selectableItemBackgroundBorderless
 */
val Context.selectableItemBackgroundBorderlessRes: Int? get() = getAttributeResource(android.R.attr.selectableItemBackgroundBorderless)

/**
 * 获取attr资源 actionBarItemBackground
 */
val Context.actionBarItemBackground: Int? get() = getAttributeResource(android.R.attr.actionBarItemBackground)

/**
 * 为Drawable着色
 */
fun Drawable.applyTint(color: Int?): Drawable {
    if (color == null) return this
    return DrawableCompat.wrap(this).mutate().apply {
        DrawableCompat.setTint(this, color)
    }
}

/**
 * 为Drawable添加Ripple效果
 */
fun Drawable.applyRipple(
    context: Context,
    rippleColor: Int? = null,
    rippleColorStateList: ColorStateList? = null
): RippleDrawable = RippleDrawable(
    rippleColorStateList
        ?: ColorStateList.valueOf(
            rippleColor
                ?: context.getAttributeResource(android.R.attr.colorControlHighlight)?.color
                ?: Color.GRAY
        ),
    this,
    null
)

/**
 * dp转px，如[xxhdpi](360 -> 1080)
 */
val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

/**
 * dp转px，如[xxhdpi](360 -> 1080)
 */
val Int.dp: Int get() = toFloat().dp.roundToInt()


/**
 * sp转px
 */
val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics
    )

/**
 * sp转px
 */
val Int.sp: Int get() = toFloat().sp.roundToInt()

/**
 * px转dp，如[xxhdpi](1080 -> 360)
 */
val Float.px2dp: Float get() = this / Resources.getSystem().displayMetrics.density + 0.5f * if (this >= 0) 1 else -1

/**
 * px转dp，如[xxhdpi](1080 -> 360)
 */
val Int.px2dp: Int get() = toFloat().px2dp.roundToInt()


/**
 * string转html
 */
val String.html: Spanned
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, 0)
        } else {
            Html.fromHtml(this)
        }

/**
 * string外包裹html color标签
 */
fun String.wrapHtmlColor(color: Int): String =
    "<font color='${color.hexColor}'>$this</font>"

/**
 * string外包裹html a标签
 */
fun String.wrapHtmlHref(url: String): String =
    "<a href='${url}'>$this</a>"


