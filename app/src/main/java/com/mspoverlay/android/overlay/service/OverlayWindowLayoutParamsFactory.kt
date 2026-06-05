package com.mspoverlay.android.overlay.service

import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager

object OverlayWindowLayoutParamsFactory {
    fun create(spec: OverlayWindowSpec = OverlayWindowSpecFactory.passiveFullScreen()): WindowManager.LayoutParams {
        var flags = 0
        if (spec.notFocusable) {
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        if (spec.notTouchable) {
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        if (spec.layoutInScreen) {
            flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        return WindowManager.LayoutParams(
            if (spec.fullScreen) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT,
            if (spec.fullScreen) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            flags,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
    }
}

