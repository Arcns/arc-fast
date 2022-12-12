package com.arc.fast.view

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*

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
fun SpannableStringBuilder.appendFastImage(fastImageSpan: FastImageSpan): SpannableStringBuilder =
    appendFastSpan(text = " ", span = fastImageSpan)
