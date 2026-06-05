package com.mspoverlay.android.overlay.model

sealed interface OverlayElement {
    val id: String?
    val type: String
    val opacity: Double
    val zIndex: Int
    val visible: Boolean
    val locked: Boolean
}

data class RectElement(
    override val id: String?,
    override val type: String,
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val rotation: Double,
    override val opacity: Double,
    override val zIndex: Int,
    override val visible: Boolean,
    override val locked: Boolean,
    val fillColor: String?,
    val strokeColor: String?,
    val strokeWidth: Double,
    val cornerRadius: Double,
) : OverlayElement

data class CircleElement(
    override val id: String?,
    override val type: String,
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val rotation: Double,
    override val opacity: Double,
    override val zIndex: Int,
    override val visible: Boolean,
    override val locked: Boolean,
    val fillColor: String?,
    val strokeColor: String?,
    val strokeWidth: Double,
) : OverlayElement

data class LineElement(
    override val id: String?,
    override val type: String,
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    override val opacity: Double,
    override val zIndex: Int,
    override val visible: Boolean,
    override val locked: Boolean,
    val strokeColor: String?,
    val strokeWidth: Double,
    val dashStyle: String?,
) : OverlayElement

