package com.example.kangchoovideowall

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView

class HDMIWebPresentation(
    context: Context,
    display: Display?,
    theme: Int = 0
) : Presentation(context, display, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window?.decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val webView = WebViewPreloader.getPreloadedWebView(context)
        (webView.parent as? ViewGroup)?.removeView(webView)

        val metrics = DisplayMetrics()
        display?.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        setContentView(webView, ViewGroup.LayoutParams(width, height))
        webView.post {
            webView.layout(0, 0, width, height)
            webView.requestLayout()
        }
    }
}
