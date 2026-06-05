package com.mspoverlay.android.core.auth.oauth

import com.mspoverlay.android.core.auth.AuthTokens
import java.net.URI
import java.net.URLDecoder

class OAuthCallbackParser {
    fun parse(callbackUrl: String, expectedState: String): OAuthCallbackResult {
        if (expectedState.isBlank()) {
            return OAuthCallbackResult.Failure("missing_expected_state")
        }

        val uri = runCatching { URI(callbackUrl) }.getOrNull()
            ?: return OAuthCallbackResult.Failure("invalid_callback_url")

        if (uri.scheme != "msp-overlay" || uri.host != "auth" || uri.path != "/callback") {
            return OAuthCallbackResult.Failure("invalid_callback_url")
        }

        val query = parseQuery(uri.rawQuery)
        val state = query["state"]
        if (state != expectedState) {
            return OAuthCallbackResult.Failure("invalid_state")
        }

        query["error"]?.takeIf { it.isNotBlank() }?.let {
            return OAuthCallbackResult.Failure(it)
        }

        val accessToken = query["accessToken"]
        if (accessToken.isNullOrBlank()) {
            return OAuthCallbackResult.Failure("missing_access_token")
        }

        return OAuthCallbackResult.Success(
            AuthTokens(
                accessToken = accessToken,
                refreshToken = query["refreshToken"],
            ),
        )
    }

    private fun parseQuery(rawQuery: String?): Map<String, String> {
        if (rawQuery.isNullOrBlank()) {
            return emptyMap()
        }

        return rawQuery.split("&")
            .filter { it.isNotBlank() }
            .associate { pair ->
                val parts = pair.split("=", limit = 2)
                val key = decode(parts[0])
                val value = if (parts.size == 2) decode(parts[1]) else ""
                key to value
            }
    }

    private fun decode(value: String): String = URLDecoder.decode(value, Charsets.UTF_8.name())
}

sealed class OAuthCallbackResult {
    data class Success(val tokens: AuthTokens) : OAuthCallbackResult()
    data class Failure(val reason: String) : OAuthCallbackResult()
}

