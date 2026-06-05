package com.mspoverlay.android.overlay.model

data class OverlayDocument(
    val schemaVersion: String,
    val overlayId: String,
    val name: String,
    val platform: String,
    val game: OverlayGame?,
    val canvas: OverlayCanvas,
    val overlaySettings: OverlaySettings,
    val elements: List<OverlayElement>,
    val meta: OverlayMeta?,
)

data class OverlayCanvas(
    val baseWidth: Double,
    val baseHeight: Double,
)

data class OverlaySettings(
    val opacity: Double = 1.0,
)

data class OverlayGame(
    val id: Long?,
    val name: String?,
)

data class OverlayMeta(
    val createdAt: String?,
    val updatedAt: String?,
)

