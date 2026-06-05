package com.mspoverlay.android.core.auth.oauth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidOAuthUrlBuilderTest {
    @Test
    fun buildsAndroidGoogleStartUrlWithEncodedCallbackAndState() {
        val url = AndroidOAuthUrlBuilder("https://api.msp-overlay.store/")
            .buildGoogleStartUrl("state 123")

        assertTrue(url.startsWith("https://api.msp-overlay.store/api/auth/android/google/start?"))
        assertTrue(url.contains("callbackUrl=msp-overlay%3A%2F%2Fauth%2Fcallback"))
        assertTrue(url.contains("state=state+123"))
    }

    @Test
    fun stateGeneratorReturnsHexString() {
        val state = OAuthStateGenerator().generate()

        assertEquals(48, state.length)
        assertTrue(state.matches(Regex("^[0-9a-f]+$")))
    }
}

