package com.arc.fast.sample.test.webview

import android.content.Context
import android.view.View
import android.webkit.*
import com.arc.fast.sample.common.data.LocalData
import com.arc.fast.sample.common.extension.LOG

class ContentWebView(
    context: Context,
    defaultUrl: String?,
    onProgress: (newProgress: Int) -> Unit,
    val onGoBack: () -> Unit,
    val onGoScan: () -> Unit,
    val onScrollToTop: (isScrollToTop: Boolean) -> Unit
) : WebView(context) {
    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        settings.javaScriptEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (!handlerOverrideUrlLoading(url)) view.loadUrl(url)
                return true
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                if (url.isNullOrBlank()) return super.shouldOverrideUrlLoading(
                    view,
                    request
                )
                LOG("WebView：$url")
                if (!handlerOverrideUrlLoading(url)) view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }
        }
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                onProgress(newProgress)
            }
        }
        LOG("WebView：${defaultUrl}")
        loadUrl(completionUrlArgs(defaultUrl))
    }

    private fun completionUrlArgs(url: String?): String {
        var newUrl = url
        if (!url.isNullOrBlank() && !LocalData.currentLoginSecret.isNullOrBlank()) {
            val andStr = if (url.contains("?")) "&" else "?"
            newUrl += andStr + "usersecre=" + LocalData.currentLoginSecret
        }
        return newUrl ?: ""
    }

    private fun handlerOverrideUrlLoading(url: String): Boolean {
        if ("m://arc.com/back".equals(url, true)) {
            onGoBack()
            return true
        } else if ("m://arc.com/scan".equals(url, true)) {
            onGoScan()
            return true
        }
        return false
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollToTop(scrollY == 0)
    }

    fun onScanResult(result: String) {
        loadUrl("javascript:getQRCode(\"$result\")")
    }
}