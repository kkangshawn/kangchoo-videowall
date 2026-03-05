package com.example.kangchoovideowall

import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.postDelayed

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var displayManager: DisplayManager
    private var externalDisplay: Display? = null
    private var externalPresentation: HDMIWebPresentation? = null
    private var isPresentationShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WebViewPreloader.preload(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.attributes = window.attributes.also {
            it.screenBrightness = 0.01f
        }

        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager

        displayManager.registerDisplayListener(object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) {
                Log.d("DISPLAY", "onDisplayAdded: $displayId")
                updateDisplayInfo()
                tryShowPresentation()
            }
            override fun onDisplayRemoved(displayId: Int) {
                Log.d("DISPLAY", "onDisplayRemoved: $displayId")
                isPresentationShowing = false
                externalPresentation?.dismiss()
                externalPresentation = null
                updateDisplayInfo()
            }
            override fun onDisplayChanged(displayId: Int) {
                Log.d("DISPLAY", "onDisplayChanged: $displayId")
                textView.postDelayed({
                    updateDisplayInfo()
                    tryShowPresentation()
                }, 500)
            }
        }, null)

        updateDisplayInfo()
        tryShowPresentation()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun tryShowPresentation() {
        if (isPresentationShowing) return
        externalDisplay?.let { display ->
            if (externalPresentation == null || externalPresentation?.display != display) {
                isPresentationShowing = true
                externalPresentation?.dismiss()
                externalPresentation = HDMIWebPresentation(this@MainActivity, display, R.style.Theme_AppCompat_Landscape)
                externalPresentation?.show()
                Log.d("DISPLAY", "Presentation shown on display ${display.displayId}")
            }
        }
    }

    private fun updateDisplayInfo() {
        val displayInfo = StringBuilder("Detected displays\n\n")

        for (display in displayManager.displays) {
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            displayInfo.append("Display ID: ${display.displayId}\n")
            displayInfo.append("Display Name: ${display.name}\n")
            displayInfo.append("Resolution: ${display.mode.physicalWidth}x${display.mode.physicalHeight}\n")
            displayInfo.append("Density: ${metrics.density}, DPI: ${metrics.densityDpi}\n")
            displayInfo.append("Refresh Rate: ${display.refreshRate} Hz\n\n")
            Log.d("DISPLAY", "id=${display.displayId}, flags=${display.flags}, name=${display.name}")
        }
        textView.text = displayInfo.toString()

        externalDisplay = displayManager.displays.firstOrNull {
            it.displayId != Display.DEFAULT_DISPLAY && it.flags and Display.FLAG_PRESENTATION != 0
        }
        Log.d("DISPLAY", "externalDisplay=$externalDisplay")
    }
}
