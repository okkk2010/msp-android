package com.mspoverlay.android.core.auth

import com.mspoverlay.android.core.network.AuthTokenProvider

class StoredAuthTokenProvider(
    private val tokenStore: TokenStore,
) : AuthTokenProvider {
    override fun getAccessToken(): String? = tokenStore.getAccessToken()
}

