package com.mspoverlay.android.overlay.service

import android.content.Context
import android.view.WindowManager
import com.mspoverlay.android.overlay.model.OverlayDocument
import com.mspoverlay.android.overlay.render.OverlayPreviewView

class OverlayWindowController(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val windowManager = appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: OverlayPreviewView? = null

    fun show(document: OverlayDocument?) {
        hide()
        val view = OverlayPreviewView(appContext).apply {
            setOverlayDocument(document)
        }
        windowManager.addView(view, OverlayWindowLayoutParamsFactory.create())
        overlayView = view
    }

    fun update(document: OverlayDocument?) {
        overlayView?.setOverlayDocument(document)
    }

    fun hide() {
        overlayView?.let { view ->
            runCatching { windowManager.removeView(view) }
        }
        overlayView = null
    }
}

