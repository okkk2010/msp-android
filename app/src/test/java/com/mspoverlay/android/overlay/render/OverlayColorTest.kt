package com.mspoverlay.android.overlay.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OverlayColorTest {
    @Test
    fun parsesHexRgbColor() {
        assertEquals(0xff2563eb.toInt(), OverlayColor.parse("#2563eb"))
    }

    @Test
    fun parsesRgbaColorAndAppliesAdditionalOpacity() {
        assertEquals(0x400a141e, OverlayColor.parse("rgba(10,20,30,0.5)", opacity = 0.5))
    }

    @Test
    fun returnsNullForInvalidColor() {
        assertNull(OverlayColor.parse("not-a-color"))
    }
}
