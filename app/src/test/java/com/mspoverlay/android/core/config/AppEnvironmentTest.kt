package com.mspoverlay.android.core.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppEnvironmentTest {
    @Test
    fun debugApiBaseUrlUsesHostedApi() {
        assertEquals("https://api.msp-overlay.store", AppEnvironment.apiBaseUrl)
    }

    @Test
    fun apiBaseUrlDoesNotEndWithSlash() {
        assertFalse(AppEnvironment.apiBaseUrl.endsWith("/"))
    }

    @Test
    fun apiBaseUrlUsesHttpsForHostedApi() {
        assertTrue(AppEnvironment.apiBaseUrl.startsWith("https://"))
    }

    @Test
    fun httpLoggingIsEnabledForDebugBuild() {
        assertTrue(AppEnvironment.httpLoggingEnabled)
    }
}
