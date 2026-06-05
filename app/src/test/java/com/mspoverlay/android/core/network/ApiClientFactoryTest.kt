package com.mspoverlay.android.core.network

import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ApiClientFactoryTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun publicClientDoesNotAttachAuthorizationHeader() {
        server.enqueue(MockResponse().setBody("ok"))
        val factory = ApiClientFactory(
            baseUrl = server.url("/").toString(),
            tokenProvider = StaticTokenProvider("expired-token"),
        )

        factory.createPublicOkHttpClient()
            .newCall(Request.Builder().url(server.url("/api/overlays")).build())
            .execute()
            .close()

        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun authenticatedClientAttachesBearerTokenWhenPresent() {
        server.enqueue(MockResponse().setBody("ok"))
        val factory = ApiClientFactory(
            baseUrl = server.url("/").toString(),
            tokenProvider = StaticTokenProvider("access-token"),
        )

        factory.createAuthenticatedOkHttpClient()
            .newCall(Request.Builder().url(server.url("/api/library")).build())
            .execute()
            .close()

        assertEquals("Bearer access-token", server.takeRequest().getHeader("Authorization"))
    }

    private class StaticTokenProvider(private val token: String?) : AuthTokenProvider {
        override fun getAccessToken(): String? = token
    }
}
