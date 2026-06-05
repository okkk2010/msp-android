package com.mspoverlay.android.core.network

interface AuthTokenProvider {
    fun getAccessToken(): String?
}

