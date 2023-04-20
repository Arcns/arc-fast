package com.arc.fast.core.extensions

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.arc.fast.core.R


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
fun String.wrapHtmlColorRes(colorRes: Int): String = wrapHtmlColor(colorRes.resToColor)

/**
 * string外包裹html color标签
 */
fun String.wrapHtmlColor(color: Int): String =
    "<font color='${color.colorToHexOrNull}'>$this</font>"

/**
 * string外包裹html a标签
 */
fun String.wrapHtmlHref(url: String): String =
    "<a href='${url}'>$this</a>"

/**
 * 监听Html Href点击事件
 */
fun TextView.setOnHtmlHrefClick(onClick:(url:String?)->Unit){
    movementMethod = CustomLinkMovementMethod().setSpanProxy(
        ClickableSpan::class.java,
        object : CustomLinkMovementMethod.SpanProxy {
            override fun proxySpan(span: Any?, widget: View?) {
                if (span is URLSpan) {
                    onClick.invoke(span.url)
                    return
                }
                if (span is ClickableSpan) {
                    span.onClick(widget!!)
                }
            }
        })
}

/**
 * 支持自定义处理的LinkMovementMethod
 */
class CustomLinkMovementMethod : LinkMovementMethod() {
    private val spansProxy = HashMap<Class<*>, SpanProxy>()
    fun setSpanProxy(spanClazz: Class<*>, proxy: SpanProxy): CustomLinkMovementMethod {
        spansProxy[spanClazz] = proxy
        return this
    }

    fun removeSpanProxy(spanClazz: Class<*>): CustomLinkMovementMethod {
        spansProxy.remove(spanClazz)
        return this
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line: Int = layout.getLineForVertical(y)
            val off: Int = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(
                off, off,
                ClickableSpan::class.java
            )
            if (link.size != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    val spanProxy = spansProxy[ClickableSpan::class.java]
                    if (spanProxy != null) {
                        spanProxy.proxySpan(link[0], widget)
                    } else {
                        link[0].onClick(widget)
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    interface SpanProxy {
        fun proxySpan(span: Any?, widget: View?)
    }
}