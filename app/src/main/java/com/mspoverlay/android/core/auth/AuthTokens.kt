package com.mspoverlay.android.core.auth

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String?,
    val tokenType: String = "Bearer",
)

