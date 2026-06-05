package com.mspoverlay.android.feature.discover.data

import com.mspoverlay.android.core.network.ApiClientFactory
import com.mspoverlay.android.core.network.AuthTokenProvider
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PublicMspApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: PublicMspApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = ApiClientFactory(
            baseUrl = server.url("/").toString(),
            tokenProvider = EmptyTokenProvider,
        ).createPublicRetrofit().create(PublicMspApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getOverlaysSendsPlatformFilterWithoutAuthorization() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "success": true,
                      "data": {
                        "content": [
                          {
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
                        ],
                        "page": 0,
                        "size": 20,
                        "totalElements": 1,
                        "totalPages": 1,
                        "first": true,
                        "last": true
                      },
                      "message": "ok"
                    }
                    """.trimIndent(),
                ),
        )

        val response = api.getOverlays(page = 0, size = 20, platform = "android")
        val request = server.takeRequest()

        assertEquals("/api/overlays?page=0&size=20&platform=android", request.path)
        assertNull(request.getHeader("Authorization"))
        assertEquals(true, response.success)
        assertEquals("ovl_android_001", response.data?.content?.first()?.overlayId)
    }

    @Test
    fun getOverlayByCodeParsesInlineOverlayJson() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "success": true,
                      "data": {
                        "id": 9,
                        "overlayId": "ovl_android_002",
                        "code": "ZXCVBN",
                        "name": "Code Overlay",
                        "description": "",
                        "platform": "android",
                        "thumbnailUrl": "/storage/overlays/ovl_android_002/thumbnail.png",
                        "schemaVersion": "1.0.0",
                        "overlayJson": "{\"schemaVersion\":\"1.0.0\",\"platform\":\"android\"}",
                        "createdAt": "2026-06-05T00:00:00Z",
                        "updatedAt": "2026-06-05T00:00:00Z"
                      },
                      "message": "ok"
                    }
                    """.trimIndent(),
                ),
        )

        val response = api.getOverlayByCode("ZXCVBN")
        val request = server.takeRequest()

        assertEquals("/api/overlays/code/ZXCVBN", request.path)
        assertEquals("ovl_android_002", response.data?.overlayId)
        assertEquals("1.0.0", response.data?.schemaVersion)
        assertEquals("{\"schemaVersion\":\"1.0.0\",\"platform\":\"android\"}", response.data?.overlayJson)
    }

    private object EmptyTokenProvider : AuthTokenProvider {
        override fun getAccessToken(): String? = null
    }
}
