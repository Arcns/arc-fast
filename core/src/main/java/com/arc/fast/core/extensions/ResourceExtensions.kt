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
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import com.arc.fast.core.FastCore
import com.arc.fast.core.util.invokeOrNull
import kotlin.math.roundToInt


/**
 * dp转px，如[xxhdpi](360 -> 1080)
 */
val Float.dpToPx: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

/**
 * dp转px，如[xxhdpi](360 -> 1080)
 */
val Int.dpToPx: Int get() = toFloat().dpToPx.roundToInt()


/**
 * sp转px
 */
val Float.spToPx: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics
    )

/**
 * sp转px
 */
val Int.spToPx: Int get() = toFloat().spToPx.roundToInt()

/**
 * px转dp，如[xxhdpi](1080 -> 360)
 */
val Float.pxToDp: Float get() = this / Resources.getSystem().displayMetrics.density + 0.5f * if (this >= 0) 1 else -1

/**
 * px转dp，如[xxhdpi](1080 -> 360)
 */
val Int.pxToDp: Int get() = toFloat().pxToDp.roundToInt()


/**
 * 获取string资源
 */
val Int.resToString: String get() = FastCore.context.getString(this)

/**
 * 获取string资源
 */
val Int.resToStringOrNull: String? get() = invokeOrNull { resToString }


/**
 * 获取string资源
 * 格式例：%1$s
 */
fun Int.resToString(vararg values: Any?): String = FastCore.context.getString(this, *values)


/**
 * 获取string资源
 * 格式例：%1$s
 */
fun Int.resToStringOrNull(vararg values: Any?): String? = invokeOrNull { resToString(*values) }

/**
 * 获取drawable资源
 */
val Int.resToDrawable: Drawable get() = ContextCompat.getDrawable(FastCore.context, this)!!

/**
 * 获取drawable资源
 */
val Int.resToDrawableOrNull: Drawable?
    get() = invokeOrNull {
        ContextCompat.getDrawable(
            FastCore.context,
            this
        )
    }


/**
 * 为Drawable着色
 */
fun Drawable.applyTint(@ColorInt color: Int?): Drawable {
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
    @ColorInt rippleColor: Int? = null,
    rippleColorStateList: ColorStateList? = null
): RippleDrawable = RippleDrawable(
    rippleColorStateList
        ?: ColorStateList.valueOf(
            rippleColor
                ?: context.getAttributeResource(android.R.attr.colorControlHighlight)?.resToColor
                ?: Color.GRAY
        ),
    this,
    null
)

/**
 * 获取color资源
 */
val Int.resToColor: Int get() = ContextCompat.getColor(FastCore.context, this)

/**
 * 获取color资源
 */
val Int.resToColorOrNull: Int? get() = invokeOrNull { resToColor }


/**
 * hex color 转 color
 */
val String.hexToColor: Int get() = Color.parseColor(if (this.startsWith("#")) this else "#$this")

/**
 * hex color 转 color
 */
val String.hexToColorOrNull: Int? get() = invokeOrNull { hexToColor }

/**
 * color 转 hex color
 */
val Int.colorToHex: String get() = "#${Integer.toHexString(this)}"

/**
 * color 转 hex color
 */
val Int.colorToHexOrNull: String? get() = invokeOrNull { colorToHex }


/**
 * 判断color是否为亮色调
 */
val Int.isLightColor get() = ColorUtils.calculateLuminance(this) >= 0.5

/**
 * color亮色度
 */
val Int.lightColorNess get() = ColorUtils.calculateLuminance(this)

/**
 * 获取dimen资源的值
 */
val Int.resToDimenValue: Float get() = FastCore.context.resources.getDimension(this)

/**
 * 获取dimen资源的值
 */
val Int.resToDimenValueOrNull: Float? get() = invokeOrNull { resToDimenValue }


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
val Context.actionBarItemBackgroundRes: Int? get() = getAttributeResource(android.R.attr.actionBarItemBackground)

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