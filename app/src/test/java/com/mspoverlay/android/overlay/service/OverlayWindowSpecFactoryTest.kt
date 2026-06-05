package com.mspoverlay.android.overlay.service

import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayWindowSpecFactoryTest {
    @Test
    fun passiveFullScreenSpecDoesNotStealFocusOrTouches() {
        val spec = OverlayWindowSpecFactory.passiveFullScreen()

        assertTrue(spec.fullScreen)
        assertTrue(spec.notFocusable)
        assertTrue(spec.notTouchable)
        assertTrue(spec.layoutInScreen)
    }
}
