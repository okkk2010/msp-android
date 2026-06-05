package com.mspoverlay.android.core.auth.oauth

import java.security.SecureRandom

class OAuthStateGenerator {
    private val secureRandom = SecureRandom()

    fun generate(): String {
        val bytes = ByteArray(24)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }
}

