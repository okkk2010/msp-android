package com.mspoverlay.android.overlay.render

data class OverlayRenderPlan(
    val commands: List<DrawCommand>,
)

sealed interface DrawCommand {
    val zIndex: Int
}

data class RectCommand(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotation: Float,
    val fillColor: Int?,
    val strokeColor: Int?,
    val strokeWidth: Float,
    val cornerRadius: Float,
    override val zIndex: Int,
) : DrawCommand

data class CircleCommand(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotation: Float,
    val fillColor: Int?,
    val strokeColor: Int?,
    val strokeWidth: Float,
    override val zIndex: Int,
) : DrawCommand

data class LineCommand(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val strokeColor: Int?,
    val strokeWidth: Float,
    val dashStyle: String?,
    override val zIndex: Int,
) : DrawCommand

