package com.mspoverlay.android.feature.discover.data

data class PlatformDto(
    val id: Long?,
    val name: String?,
    val slug: String?,
)

data class GameDto(
    val id: Long?,
    val name: String?,
    val displayName: String?,
    val slug: String?,
    val platform: String?,
)

data class OverlaySummaryDto(
    val id: Long,
    val overlayId: String,
    val code: String?,
    val name: String?,
    val description: String?,
    val platform: String?,
    val game: String?,
    val thumbnailPath: String?,
    val authorName: String?,
    val createdAt: String?,
    val updatedAt: String?,
)

data class OverlayAuthorDto(
    val id: Long?,
    val name: String?,
)

data class OverlayDetailDto(
    val id: Long,
    val overlayId: String,
    val code: String?,
    val name: String?,
    val description: String?,
    val platform: String?,
    val game: String?,
    val schemaVersion: String?,
    val canvasBaseWidth: Int?,
    val canvasBaseHeight: Int?,
    val opacity: Double?,
    val jsonPath: String?,
    val thumbnailPath: String?,
    val author: OverlayAuthorDto?,
    val createdAt: String?,
    val updatedAt: String?,
)

data class OverlayCodeLoadDto(
    val id: Long,
    val overlayId: String,
    val code: String?,
    val name: String?,
    val description: String?,
    val platform: String?,
    val thumbnailUrl: String?,
    val schemaVersion: String?,
    val overlayJson: String?,
    val createdAt: String?,
    val updatedAt: String?,
)

