package com.arc.fast.span

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.*
import android.widget.TextView
import androidx.annotation.DrawableRes

/**
 * 启用ClickableSpan
 */
fun TextView.enableClickableSpan() {
    highlightColor = Color.TRANSPARENT
    movementMethod = LinkMovementMethod.getInstance()
}

/**
 * 快速设置Span
 */
fun SpannableStringBuilder.appendFastSpan(
    text: CharSequence,
    span: ReplacementSpan,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
): SpannableStringBuilder = appendFastSpan(
    text = text,
    spans = arrayListOf(span),
    flags = flags
)

/**
 * 快速设置Span
 */
fun SpannableStringBuilder.appendFastSpan(
    text: CharSequence,
    spans: List<Any>,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
): SpannableStringBuilder {
    val start = length
    val end = start + text.length
    append(text)
    spans.forEach {
        setSpan(it, start, end, flags)
    }
    return this
}

/**
 * 快速设置常用的文本样式Span
 */
fun SpannableStringBuilder.appendFastTextStyle(
    text: CharSequence,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    onApplyFastTextStyle: (FastTextStyle.() -> Unit)
): SpannableStringBuilder = appendFastSpan(
    text = text,
    spans = FastTextStyle().apply {
        onApplyFastTextStyle.invoke(this)
    }.spans,
    flags = flags
)


/**
 * 快速设置间隔Span
 */
fun SpannableStringBuilder.appendFastSpacing(size: Int): SpannableStringBuilder =
    appendFastSpan(text = " ", span = FastSpacingSpan(size))

/**
 * 快速设置图片Span
 */
fun SpannableStringBuilder.appendFastImageStyle(
    context: Context,
    @DrawableRes drawableRes: Int,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    onApplyFastImageStyle: (FastImageSpanStyle.() -> Unit)
): SpannableStringBuilder = appendFastSpan(
    text = " ",
    spans = FastImageSpanStyle(context, drawableRes).apply {
        onApplyFastImageStyle.invoke(this)
    }.spans,
    flags = flags
)

/**
 * 快速设置图片Span
 */
fun SpannableStringBuilder.appendFastImageStyle(
    drawable: Drawable,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    onApplyFastImageStyle: (FastImageSpanStyle.() -> Unit)
): SpannableStringBuilder = appendFastSpan(
    text = " ",
    spans = FastImageSpanStyle(drawable).apply {
        onApplyFastImageStyle.invoke(this)
    }.spans,
    flags = flags
)