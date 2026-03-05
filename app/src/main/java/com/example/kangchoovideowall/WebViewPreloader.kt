package com.example.kangchoovideowall

import android.content.Context
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import java.lang.ref.WeakReference

object WebViewPreloader {
    private var preloadedWebViewRef: WeakReference<WebView>? = null

    private fun createWebView(context: Context): WebView {
        val webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript("document.documentElement.style.zoom = '0.60';", null)
            }
        }
        webView.loadUrl("https://videowall.kkangshawn.com")
        return webView
    }

    fun preload(context: Context) {
        if (preloadedWebViewRef?.get() != null) return
        preloadedWebViewRef = WeakReference(createWebView(context))
    }

    fun getPreloadedWebView(context: Context): WebView {
        return preloadedWebViewRef?.get() ?: createWebView(context)
            .also { preloadedWebViewRef = WeakReference(it) }
    }
}
