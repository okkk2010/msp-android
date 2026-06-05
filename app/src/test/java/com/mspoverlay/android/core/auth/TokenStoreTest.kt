package com.mspoverlay.android.core.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TokenStoreTest {
    @Test
    fun tokenProviderReadsAccessTokenFromStore() {
        val store = InMemoryTokenStore()
        store.save(AuthTokens(accessToken = "access", refreshToken = "refresh"))

        val provider = StoredAuthTokenProvider(store)

        assertEquals("access", provider.getAccessToken())
    }

    @Test
    fun clearRemovesStoredTokens() {
        val store = InMemoryTokenStore()
        store.save(AuthTokens(accessToken = "access", refreshToken = "refresh"))

        store.clear()

        assertNull(store.getAccessToken())
        assertNull(store.getRefreshToken())
    }

    private class InMemoryTokenStore : TokenStore {
        private var tokens: AuthTokens? = null

        override fun save(tokens: AuthTokens) {
            this.tokens = tokens
        }

        override fun getAccessToken(): String? = tokens?.accessToken

        override fun getRefreshToken(): String? = tokens?.refreshToken

        override fun clear() {
            tokens = null
        }
    }
}
