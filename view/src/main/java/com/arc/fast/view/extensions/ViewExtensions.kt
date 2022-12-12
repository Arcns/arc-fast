package com.arc.fast.view.extensions

import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * 从父控件中删除自己
 */
fun View.removeSelfFromParent() {
    (parent as? ViewGroup)?.removeView(this)
}


/**
 * TextView自定义a标签（URLSpan）点击事件处理
 */
fun TextView.handleUrlClicks(onClicked: ((String) -> Unit)? = null) {
    //create span builder and replaces current text with it
    text = text.handleUrlClicks(onClicked)
    //make sure movement method is set
    movementMethod = LinkMovementMethod.getInstance()
}

/**
 * 为CharSequence的a标签（URLSpan）添加自定义点击事件处理
 */
fun CharSequence.handleUrlClicks(onClicked: ((String) -> Unit)? = null): CharSequence =
    SpannableStringBuilder.valueOf(this).apply {
        //search for all URL spans and replace all spans with our own clickable spans
        getSpans(0, length, URLSpan::class.java).forEach {
            //add new clickable span at the same position
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url)
                    }
                },
                getSpanStart(it),
                getSpanEnd(it),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            //remove old URLSpan
            removeSpan(it)
        }
    }


/**
 * 设置文本字体为中粗
 */
fun TextView.setTextMediumBold() = this.setTextMediumBold(1f)
fun TextView.setTextMediumBold(mediumWeight: Float) {
    paint.style = Paint.Style.FILL_AND_STROKE
    paint.strokeWidth = mediumWeight
    invalidate()
}

/**
 * 禁用文本字体中粗
 */
fun TextView.disableTextMediumBold() = setTextMediumBold(0f)