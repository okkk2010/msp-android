package com.mspoverlay.android.overlay.service

data class OverlayWindowSpec(
    val fullScreen: Boolean,
    val notFocusable: Boolean,
    val notTouchable: Boolean,
    val layoutInScreen: Boolean,
)

object OverlayWindowSpecFactory {
    fun passiveFullScreen(): OverlayWindowSpec {
        return OverlayWindowSpec(
            fullScreen = true,
            notFocusable = true,
            notTouchable = true,
            layoutInScreen = true,
        )
    }
}

