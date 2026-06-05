package com.mspoverlay.android.core.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPreferencesTokenStore(
    context: Context,
) : TokenStore {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun save(tokens: AuthTokens) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .putString(KEY_TOKEN_TYPE, tokens.tokenType)
            .apply()
    }

    override fun getAccessToken(): String? = preferences.getString(KEY_ACCESS_TOKEN, null)

    override fun getRefreshToken(): String? = preferences.getString(KEY_REFRESH_TOKEN, null)

    override fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "msp_auth_tokens"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_TOKEN_TYPE = "token_type"
    }
}

