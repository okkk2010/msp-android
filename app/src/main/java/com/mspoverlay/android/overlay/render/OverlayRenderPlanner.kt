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
        val uniformScale = minOf(scaleX, scaleY)
        val offsetX = (targetWidth - document.canvas.baseWidth * uniformScale) / 2.0
        val offsetY = (targetHeight - document.canvas.baseHeight * uniformScale) / 2.0
        val safeFrame = RenderFrame(
            left = offsetX,
            top = offsetY,
            right = offsetX + document.canvas.baseWidth * uniformScale,
            bottom = offsetY + document.canvas.baseHeight * uniformScale,
        )
        val screenFrame = RenderFrame(
            left = 0.0,
            top = 0.0,
            right = targetWidth.toDouble(),
            bottom = targetHeight.toDouble(),
        )
        val strokeScale = (scaleX + scaleY) / 2.0
        val globalOpacity = document.overlaySettings.opacity.coerceIn(0.0, 1.0)

        val commands = document.elements
            .filter { it.visible }
            .sortedBy { it.zIndex }
            .mapNotNull { element ->
                val opacity = (globalOpacity * element.opacity.coerceIn(0.0, 1.0)).coerceIn(0.0, 1.0)
                when (element) {
                    is RectElement -> {
                        val bounds = anchoredBounds(
                            x = element.x,
                            y = element.y,
                            width = element.width,
                            height = element.height,
                            baseWidth = document.canvas.baseWidth,
                            baseHeight = document.canvas.baseHeight,
                            scale = uniformScale,
                            safeFrame = safeFrame,
                            screenFrame = screenFrame,
                            anchor = element.anchor,
                            anchorSpace = element.anchorSpace,
                        )
                        RectCommand(
                            x = bounds.x,
                            y = bounds.y,
                            width = bounds.width,
                            height = bounds.height,
                            rotation = element.rotation.toFloat(),
                            fillColor = OverlayColor.parse(element.fillColor, opacity),
                            strokeColor = OverlayColor.parse(element.strokeColor, opacity),
                            strokeWidth = (element.strokeWidth * uniformScale).toFloat(),
                            cornerRadius = (element.cornerRadius * uniformScale).toFloat(),
                            zIndex = element.zIndex,
                        )
                    }
                    is CircleElement -> {
                        val bounds = anchoredBounds(
                            x = element.x,
                            y = element.y,
                            width = element.width,
                            height = element.height,
                            baseWidth = document.canvas.baseWidth,
                            baseHeight = document.canvas.baseHeight,
                            scale = uniformScale,
                            safeFrame = safeFrame,
                            screenFrame = screenFrame,
                            anchor = element.anchor,
                            anchorSpace = element.anchorSpace,
                        )
                        CircleCommand(
                            x = bounds.x,
                            y = bounds.y,
                            width = bounds.width,
                            height = bounds.height,
                            rotation = element.rotation.toFloat(),
                            fillColor = OverlayColor.parse(element.fillColor, opacity),
                            strokeColor = OverlayColor.parse(element.strokeColor, opacity),
                            strokeWidth = (element.strokeWidth * uniformScale).toFloat(),
                            zIndex = element.zIndex,
                        )
                    }
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

    private fun anchoredBounds(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        baseWidth: Double,
        baseHeight: Double,
        scale: Double,
        safeFrame: RenderFrame,
        screenFrame: RenderFrame,
        anchor: String,
        anchorSpace: String,
    ): RenderBounds {
        val frame = if (anchorSpace == "screen") screenFrame else safeFrame
        val renderWidth = width * scale
        val renderHeight = height * scale
        val marginRight = baseWidth - x - width
        val marginBottom = baseHeight - y - height
        val centerOffsetX = (x + width / 2.0) - baseWidth / 2.0
        val centerOffsetY = (y + height / 2.0) - baseHeight / 2.0

        val renderX = when (anchor) {
            "top-right", "right", "bottom-right" -> frame.right - marginRight * scale - renderWidth
            "top", "center", "bottom" -> frame.centerX + centerOffsetX * scale - renderWidth / 2.0
            else -> frame.left + x * scale
        }

        val renderY = when (anchor) {
            "bottom-left", "bottom", "bottom-right" -> frame.bottom - marginBottom * scale - renderHeight
            "left", "center", "right" -> frame.centerY + centerOffsetY * scale - renderHeight / 2.0
            else -> frame.top + y * scale
        }

        return RenderBounds(
            x = renderX.toFloat(),
            y = renderY.toFloat(),
            width = renderWidth.toFloat(),
            height = renderHeight.toFloat(),
        )
    }
}

private data class RenderFrame(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
) {
    val centerX: Double = (left + right) / 2.0
    val centerY: Double = (top + bottom) / 2.0
}

private data class RenderBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)
