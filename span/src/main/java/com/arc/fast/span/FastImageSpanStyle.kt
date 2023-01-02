package com.arc.fast.span

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

data class FastImageSpanStyle(
    // 图片
    var drawable: Drawable
) {
    constructor(context: Context, @DrawableRes drawableRes: Int) : this(
        ContextCompat.getDrawable(
            context,
            drawableRes
        )!!
    )


    // padding
    var padding: Int? = null
    var paddingLeft: Int? = null
    var paddingRight: Int? = null
    var paddingTop: Int? = null
    var paddingBottom: Int? = null

    // 宽度
    var width: Int? = null

    // 高度
    var height: Int? = null

    // 启用垂直居中图片
    var enableVerticalCenterImage: Boolean? = null

    // 点击事件
    var onClick: ((View) -> Unit)? = null


    val spans: List<CharacterStyle>
        get() = arrayListOf<CharacterStyle>().apply {
            var image = drawable
            if (width != null && height != null) {
                image.setBounds(0, 0, width!!, height!!)
                val paddingLeft = paddingLeft ?: padding ?: 0
                val paddingRight = paddingRight ?: padding ?: 0
                val paddingTop = paddingTop ?: padding ?: 0
                val paddingBottom = paddingBottom ?: padding ?: 0
                if (paddingLeft != 0 || paddingRight != 0 || paddingTop != 0 || paddingBottom != 0) {
                    image = InsetDrawable(
                        image,
                        paddingLeft,
                        paddingTop,
                        paddingRight,
                        paddingBottom
                    ).apply {
                        setBounds(
                            0,
                            0,
                            width!! + paddingLeft + paddingRight,
                            height!! + paddingTop + paddingBottom
                        )
                    }
                }
            }
            add(
                if (enableVerticalCenterImage != false) FastVerticalCenterImageSpan(image)
                else ImageSpan(image)
            )
            if (onClick != null) add(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick!!.invoke(widget)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                }
            })
        }
}