package com.mspoverlay.android.feature.library.data

import com.mspoverlay.android.feature.discover.data.OverlaySummaryDto

data class UserMeDto(
    val id: Long,
    val oauthProvider: String?,
    val email: String?,
    val name: String?,
    val profileImageUrl: String?,
)

data class AuthTokenDto(
    val accessToken: String,
    val refreshToken: String?,
    val tokenType: String?,
    val refreshTokenExpiresAt: String?,
)

data class RefreshTokenRequestDto(
    val refreshToken: String,
)

data class LibraryItemDto(
    val libraryId: Long,
    val savedAt: String?,
    val overlay: OverlaySummaryDto,
)

data class LibrarySaveRequestDto(
    val overlayId: Long,
)

