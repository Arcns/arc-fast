package com.arc.fast.span

import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.text.style.*
import android.view.View
import android.widget.TextView

/**
 * 快速设置文本样式
 */
open class FastTextStyle(
    // 文字颜色
    var textColor: Int? = null,
    // 文字大小
    var textSize: Int? = null,
    // Typeface
    var textStyle: Int? = null,
    // 文字点击事件
    var onClick: ((View) -> Unit)? = null,
    // 文字下划线
    var underlineColor: Int? = null,
    // 文字中粗体
    var textMediumBold: Float? = null
) {
    fun setTextMediumBold() = setTextMediumBold(1f)
    fun setTextMediumBold(mediumWeight: Float) {
        this.textMediumBold = textMediumBold
    }

    fun disableTextMediumBold() = setTextMediumBold(0f)

    val spans: List<CharacterStyle>
        get() = arrayListOf<CharacterStyle>().apply {
            if (textColor != null) add(ForegroundColorSpan(textColor!!))
            if (textSize != null) add(AbsoluteSizeSpan(textSize!!))
            if (textStyle != null) add(StyleSpan(textStyle!!))
            if (onClick != null) add(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick!!.invoke(widget)
                }

                override fun updateDrawState(ds: TextPaint) {
                    var hasUpdate = false
                    if (underlineColor != null) {
                        hasUpdate = true
                        if (underlineColor == Color.TRANSPARENT) {
                            ds.color = ds.linkColor
                            ds.isUnderlineText = false
                        } else {
                            ds.color = underlineColor!!
                            ds.isUnderlineText = true
                        }
                    }
                    if (textMediumBold != null) {
                        ds.style = Paint.Style.FILL_AND_STROKE
                        ds.strokeWidth = textMediumBold!!
                    }
                    if (!hasUpdate) super.updateDrawState(ds)
                }
            })
        }
}