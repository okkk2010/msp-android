package com.mspoverlay.android.overlay.render

import com.mspoverlay.android.overlay.model.CircleElement
import com.mspoverlay.android.overlay.model.LineElement
import com.mspoverlay.android.overlay.model.OverlayDocument
import com.mspoverlay.android.overlay.model.RectElement

class OverlayRenderPlanner {
    fun plan(document: OverlayDocument, targetWidth: Int, targetHeight: Int): OverlayRenderPlan {
        if (targetWidth <= 0 || targetHeight <= 0) {
            return OverlayRenderPlan(emptyList())
        }

        val scaleX = targetWidth / document.canvas.baseWidth
        val scaleY = targetHeight / document.canvas.baseHeight
        val strokeScale = (scaleX + scaleY) / 2.0
        val globalOpacity = document.overlaySettings.opacity.coerceIn(0.0, 1.0)

        val commands = document.elements
            .filter { it.visible }
            .sortedBy { it.zIndex }
            .mapNotNull { element ->
                val opacity = (globalOpacity * element.opacity.coerceIn(0.0, 1.0)).coerceIn(0.0, 1.0)
                when (element) {
                    is RectElement -> RectCommand(
                        x = (element.x * scaleX).toFloat(),
                        y = (element.y * scaleY).toFloat(),
                        width = (element.width * scaleX).toFloat(),
                        height = (element.height * scaleY).toFloat(),
                        rotation = element.rotation.toFloat(),
                        fillColor = OverlayColor.parse(element.fillColor, opacity),
                        strokeColor = OverlayColor.parse(element.strokeColor, opacity),
                        strokeWidth = (element.strokeWidth * strokeScale).toFloat(),
                        cornerRadius = (element.cornerRadius * strokeScale).toFloat(),
                        zIndex = element.zIndex,
                    )
                    is CircleElement -> CircleCommand(
                        x = (element.x * scaleX).toFloat(),
                        y = (element.y * scaleY).toFloat(),
                        width = (element.width * scaleX).toFloat(),
                        height = (element.height * scaleY).toFloat(),
                        rotation = element.rotation.toFloat(),
                        fillColor = OverlayColor.parse(element.fillColor, opacity),
                        strokeColor = OverlayColor.parse(element.strokeColor, opacity),
                        strokeWidth = (element.strokeWidth * strokeScale).toFloat(),
                        zIndex = element.zIndex,
                    )
                    is LineElement -> LineCommand(
                        x1 = (element.x1 * scaleX).toFloat(),
                        y1 = (element.y1 * scaleY).toFloat(),
                        x2 = (element.x2 * scaleX).toFloat(),
                        y2 = (element.y2 * scaleY).toFloat(),
                        strokeColor = OverlayColor.parse(element.strokeColor, opacity),
                        strokeWidth = (element.strokeWidth * strokeScale).toFloat(),
                        dashStyle = element.dashStyle,
                        zIndex = element.zIndex,
                    )
                }
            }

        return OverlayRenderPlan(commands)
    }
}

