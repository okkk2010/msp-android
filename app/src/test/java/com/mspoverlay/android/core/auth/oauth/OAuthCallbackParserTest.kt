package com.mspoverlay.android.core.auth.oauth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OAuthCallbackParserTest {
    private val parser = OAuthCallbackParser()

    @Test
    fun parsesSuccessfulCallback() {
        val result = parser.parse(
            callbackUrl = "msp-overlay://auth/callback?accessToken=access&refreshToken=refresh&state=abc",
            expectedState = "abc",
        )

        assertTrue(result is OAuthCallbackResult.Success)
        result as OAuthCallbackResult.Success
        assertEquals("access", result.tokens.accessToken)
        assertEquals("refresh", result.tokens.refreshToken)
    }

    @Test
    fun rejectsMismatchedState() {
        val result = parser.parse(
            callbackUrl = "msp-overlay://auth/callback?accessToken=access&state=wrong",
            expectedState = "abc",
        )

        assertEquals(OAuthCallbackResult.Failure("invalid_state"), result)
    }

    @Test
    fun returnsServerErrorReason() {
        val result = parser.parse(
            callbackUrl = "msp-overlay://auth/callback?error=oauth_login_failed&state=abc",
            expectedState = "abc",
        )

        assertEquals(OAuthCallbackResult.Failure("oauth_login_failed"), result)
    }
}
