package com.mspoverlay.android.overlay.parser

import com.mspoverlay.android.overlay.model.CircleElement
import com.mspoverlay.android.overlay.model.LineElement
import com.mspoverlay.android.overlay.model.RectElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayJsonParserTest {
    private val parser = OverlayJsonParser()

    @Test
    fun parsesAndroidOverlayWithSupportedElements() {
        val document = parser.parse(validOverlayJson())

        assertEquals("1.0.0", document.schemaVersion)
        assertEquals("ovl_android_001", document.overlayId)
        assertEquals("android", document.platform)
        assertEquals(1920.0, document.canvas.baseWidth, 0.0)
        assertEquals(0.85, document.overlaySettings.opacity, 0.0)
        assertTrue(document.elements[0] is RectElement)
        assertTrue(document.elements[1] is CircleElement)
        assertTrue(document.elements[2] is LineElement)
    }

    @Test
    fun rejectsNonAndroidPlatform() {
        val error = runCatching {
            parser.parse(validOverlayJson().replace("\"platform\":\"android\"", "\"platform\":\"windows\""))
        }.exceptionOrNull()

        assertTrue(error is OverlayJsonParseException)
        assertEquals("Unsupported overlay platform.", error?.message)
    }

    @Test
    fun rejectsUnsupportedElementType() {
        val error = runCatching {
            parser.parse(validOverlayJson().replace("\"type\":\"line\"", "\"type\":\"text\""))
        }.exceptionOrNull()

        assertTrue(error is OverlayJsonParseException)
        assertEquals("Unsupported element type: text", error?.message)
    }

    @Test
    fun rejectsInvalidCanvasSize() {
        val error = runCatching {
            parser.parse(validOverlayJson().replace("\"baseWidth\":1920", "\"baseWidth\":0"))
        }.exceptionOrNull()

        assertTrue(error is OverlayJsonParseException)
        assertEquals("canvas size is invalid.", error?.message)
    }

    private fun validOverlayJson(): String {
        return """
            {
              "schemaVersion":"1.0.0",
              "overlayId":"ovl_android_001",
              "name":"Android Overlay",
              "platform":"android",
              "game":{"id":1,"name":"Sample Game"},
              "canvas":{"baseWidth":1920,"baseHeight":1080},
              "overlaySettings":{"opacity":0.85},
              "elements":[
                {
                  "id":"rect-1",
                  "type":"rect",
                  "x":10,
                  "y":20,
                  "width":100,
                  "height":50,
                  "rotation":0,
                  "opacity":1,
                  "zIndex":1,
                  "visible":true,
                  "locked":false,
                  "fillColor":"#2563EB",
                  "strokeColor":"#0F172A",
                  "strokeWidth":2,
                  "cornerRadius":8
                },
                {
                  "id":"circle-1",
                  "type":"circle",
                  "x":140,
                  "y":20,
                  "width":60,
                  "height":60,
                  "opacity":0.5,
                  "zIndex":2,
                  "fillColor":"rgba(10,20,30,0.5)",
                  "strokeColor":"#000000",
                  "strokeWidth":1
                },
                {
                  "id":"line-1",
                  "type":"line",
                  "x1":0,
                  "y1":0,
                  "x2":200,
                  "y2":200,
                  "opacity":1,
                  "zIndex":3,
                  "strokeColor":"#FF00FF",
                  "strokeWidth":4,
                  "dashStyle":"dash"
                }
              ],
              "meta":{"createdAt":"2026-06-05T00:00:00Z","updatedAt":"2026-06-05T00:00:00Z"}
            }
        """.trimIndent()
    }
}
