package com.mspoverlay.android.core.auth.oauth

import java.net.URLEncoder

class AndroidOAuthUrlBuilder(
    private val apiBaseUrl: String,
) {
    fun buildGoogleStartUrl(state: String): String {
        require(state.isNotBlank()) { "state is required." }
        val base = apiBaseUrl.trimEnd('/')
        val callback = encode(OAuthConfig.CALLBACK_URL)
        val encodedState = encode(state)
        return "$base/${OAuthConfig.ANDROID_GOOGLE_START_PATH}?callbackUrl=$callback&state=$encodedState"
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}

