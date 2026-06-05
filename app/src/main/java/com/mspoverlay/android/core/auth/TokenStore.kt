package com.mspoverlay.android.core.auth

interface TokenStore {
    fun save(tokens: AuthTokens)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}

