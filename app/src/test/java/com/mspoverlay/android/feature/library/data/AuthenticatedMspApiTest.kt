package com.mspoverlay.android.feature.library.data

import com.mspoverlay.android.core.network.ApiClientFactory
import com.mspoverlay.android.core.network.AuthTokenProvider
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthenticatedMspApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: AuthenticatedMspApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = ApiClientFactory(
            baseUrl = server.url("/").toString(),
            tokenProvider = StaticTokenProvider("access-token"),
        ).createAuthenticatedRetrofit().create(AuthenticatedMspApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getLibraryUsesAuthenticatedClientAndParsesItems() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "success": true,
                      "data": [
                        {
                          "libraryId": 100,
                          "savedAt": "2026-06-05T00:00:00Z",
                          "overlay": {
                            "id": 8,
                            "overlayId": "ovl_android_001",
                            "code": "ABC123",
                            "name": "Android Overlay",
                            "description": "test",
                            "platform": "android",
                            "game": "sample-game",
                            "thumbnailPath": "/storage/overlays/ovl_android_001/thumbnail.png",
                            "authorName": "Tester",
                            "createdAt": "2026-06-05T00:00:00Z",
                            "updatedAt": "2026-06-05T00:00:00Z"
                          }
                        }
                      ],
                      "message": "ok"
                    }
                    """.trimIndent(),
                ),
        )

        val response = api.getLibrary()
        val request = server.takeRequest()

        assertEquals("/api/library", request.path)
        assertEquals("Bearer access-token", request.getHeader("Authorization"))
        assertEquals(100L, response.data?.first()?.libraryId)
        assertEquals("ovl_android_001", response.data?.first()?.overlay?.overlayId)
    }

    @Test
    fun saveToLibraryPostsNumericOverlayId() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":true,"data":null,"message":"ok"}"""),
        )

        api.saveToLibrary(LibrarySaveRequestDto(8))
        val request = server.takeRequest()

        assertEquals("/api/library", request.path)
        assertEquals("POST", request.method)
        assertEquals("Bearer access-token", request.getHeader("Authorization"))
        assertEquals("""{"overlayId":8}""", request.body.readUtf8())
    }

    @Test
    fun deleteFromLibraryUsesOverlayDatabaseIdPath() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":true,"data":null,"message":"ok"}"""),
        )

        api.deleteFromLibrary(8)
        val request = server.takeRequest()

        assertEquals("/api/library/8", request.path)
        assertEquals("DELETE", request.method)
    }

    private class StaticTokenProvider(private val token: String?) : AuthTokenProvider {
        override fun getAccessToken(): String? = token
    }
}
