package com.arc.fast.view

import android.graphics.Color
import android.text.TextPaint
import android.text.style.*
import android.view.View

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
    var underlineColor: Int? = null
) {
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
                    if (underlineColor == null) {
                        super.updateDrawState(ds)
                    } else {
                        if (underlineColor == Color.TRANSPARENT) {
                            ds.color = ds.linkColor
                            ds.isUnderlineText = false
                        } else {
                            ds.color = underlineColor!!
                            ds.isUnderlineText = true
                        }
                    }
                }
            })
        }
}