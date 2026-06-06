package com.mspoverlay.android.overlay.render

import com.mspoverlay.android.overlay.model.LineElement
import com.mspoverlay.android.overlay.model.OverlayCanvas
import com.mspoverlay.android.overlay.model.OverlayDocument
import com.mspoverlay.android.overlay.model.OverlaySettings
import com.mspoverlay.android.overlay.model.RectElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayRenderPlannerTest {
    private val planner = OverlayRenderPlanner()

    @Test
    fun createsScaledCommandsSortedByZIndexAndIgnoresInvisibleElements() {
        val document = OverlayDocument(
            schemaVersion = "1.0.0",
            overlayId = "ovl_android_001",
            name = "Android Overlay",
            platform = "android",
            game = null,
            canvas = OverlayCanvas(baseWidth = 200.0, baseHeight = 100.0),
            overlaySettings = OverlaySettings(opacity = 0.5),
            elements = listOf(
                RectElement(
                    id = "hidden",
                    type = "rect",
                    x = 0.0,
                    y = 0.0,
                    width = 10.0,
                    height = 10.0,
                    anchor = "top-left",
                    anchorSpace = "safeFrame",
                    rotation = 0.0,
                    opacity = 1.0,
                    zIndex = 0,
                    visible = false,
                    locked = false,
                    fillColor = "#ffffff",
                    strokeColor = null,
                    strokeWidth = 0.0,
                    cornerRadius = 0.0,
                ),
                LineElement(
                    id = "line",
                    type = "line",
                    x1 = 10.0,
                    y1 = 10.0,
                    x2 = 20.0,
                    y2 = 20.0,
                    opacity = 1.0,
                    zIndex = 2,
                    visible = true,
                    locked = false,
                    strokeColor = "#ff00ff",
                    strokeWidth = 2.0,
                    dashStyle = "dash",
                ),
                RectElement(
                    id = "rect",
                    type = "rect",
                    x = 10.0,
                    y = 5.0,
                    width = 20.0,
                    height = 10.0,
                    anchor = "top-left",
                    anchorSpace = "safeFrame",
                    rotation = 15.0,
                    opacity = 0.5,
                    zIndex = 1,
                    visible = true,
                    locked = false,
                    fillColor = "#2563eb",
                    strokeColor = "#0f172a",
                    strokeWidth = 4.0,
                    cornerRadius = 6.0,
                ),
            ),
            meta = null,
        )

        val plan = planner.plan(document, targetWidth = 400, targetHeight = 300)

        assertEquals(2, plan.commands.size)
        assertTrue(plan.commands[0] is RectCommand)
        assertTrue(plan.commands[1] is LineCommand)

        val rect = plan.commands[0] as RectCommand
        assertEquals(20f, rect.x)
        assertEquals(60f, rect.y)
        assertEquals(40f, rect.width)
        assertEquals(20f, rect.height)
        assertEquals(8f, rect.strokeWidth)
        assertEquals(12f, rect.cornerRadius)
    }

    @Test
    fun anchorsRectToScreenRightEdgeWhenAnchorSpaceIsScreen() {
        val document = OverlayDocument(
            schemaVersion = "1.0.0",
            overlayId = "ovl_android_001",
            name = "Android Overlay",
            platform = "android",
            game = null,
            canvas = OverlayCanvas(baseWidth = 200.0, baseHeight = 100.0),
            overlaySettings = OverlaySettings(opacity = 1.0),
            elements = listOf(
                RectElement(
                    id = "rect",
                    type = "rect",
                    x = 180.0,
                    y = 10.0,
                    width = 20.0,
                    height = 10.0,
                    anchor = "top-right",
                    anchorSpace = "screen",
                    rotation = 0.0,
                    opacity = 1.0,
                    zIndex = 0,
                    visible = true,
                    locked = false,
                    fillColor = "#2563eb",
                    strokeColor = null,
                    strokeWidth = 0.0,
                    cornerRadius = 0.0,
                ),
            ),
            meta = null,
        )

        val plan = planner.plan(document, targetWidth = 400, targetHeight = 300)
        val rect = plan.commands[0] as RectCommand

        assertEquals(360f, rect.x)
        assertEquals(20f, rect.y)
        assertEquals(40f, rect.width)
        assertEquals(20f, rect.height)
    }

    @Test
    fun returnsEmptyPlanForInvalidTargetSize() {
        val document = OverlayDocument(
            schemaVersion = "1.0.0",
            overlayId = "ovl_android_001",
            name = "Android Overlay",
            platform = "android",
            game = null,
            canvas = OverlayCanvas(baseWidth = 200.0, baseHeight = 100.0),
            overlaySettings = OverlaySettings(opacity = 1.0),
            elements = emptyList(),
            meta = null,
        )

        assertEquals(emptyList<DrawCommand>(), planner.plan(document, 0, 100).commands)
    }
}
