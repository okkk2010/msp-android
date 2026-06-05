package com.mspoverlay.android.core.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppEnvironmentTest {
    @Test
    fun debugApiBaseUrlUsesAndroidEmulatorHost() {
        assertEquals("http://10.0.2.2:8080", AppEnvironment.apiBaseUrl)
    }

    @Test
    fun apiBaseUrlDoesNotEndWithSlash() {
        assertFalse(AppEnvironment.apiBaseUrl.endsWith("/"))
    }

    @Test
    fun apiBaseUrlIsHttpOnlyForDebugBuild() {
        assertTrue(AppEnvironment.apiBaseUrl.startsWith("http://"))
    }
}
