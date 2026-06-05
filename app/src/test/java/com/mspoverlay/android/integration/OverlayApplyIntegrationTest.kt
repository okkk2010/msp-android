package com.mspoverlay.android.integration

import com.mspoverlay.android.core.network.ApiClientFactory
import com.mspoverlay.android.core.network.AuthTokenProvider
import com.mspoverlay.android.feature.discover.data.PublicMspApi
import com.mspoverlay.android.overlay.apply.OverlayApplyRequestFactory
import com.mspoverlay.android.overlay.apply.OverlayApplyRequestResult
import com.mspoverlay.android.overlay.cache.CachedOverlay
import com.mspoverlay.android.overlay.cache.FileOverlayCache
import com.mspoverlay.android.overlay.parser.OverlayJsonParser
import com.mspoverlay.android.overlay.render.OverlayRenderPlanner
import java.nio.file.Files
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OverlayApplyIntegrationTest {
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
    fun codeLoadToCacheToRenderPlanFlowWorksWithoutAuthHeader() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "success": true,
                      "data": {
                        "id": 1,
                        "overlayId": "ovl_android_001",
                        "code": "ABC123",
                        "name": "Android Overlay",
                        "description": "",
                        "platform": "android",
                        "thumbnailUrl": "/storage/overlays/ovl_android_001/thumbnail.png",
                        "schemaVersion": "1.0.0",
                        "overlayJson": ${jsonString(overlayJson())},
                        "createdAt": "2026-06-05T00:00:00Z",
                        "updatedAt": "2026-06-05T00:00:00Z"
                      },
                      "message": "ok"
                    }
                    """.trimIndent(),
                ),
        )
        val api = ApiClientFactory(
            baseUrl = server.url("/").toString(),
            tokenProvider = StaticTokenProvider("stale-token"),
        ).createPublicRetrofit().create(PublicMspApi::class.java)

        val codeResponse = api.getOverlayByCode("ABC123")
        val request = server.takeRequest()
        assertEquals("/api/overlays/code/ABC123", request.path)
        assertEquals(null, request.getHeader("Authorization"))

        val applyResult = OverlayApplyRequestFactory().fromCodeLoad(codeResponse.data!!)
        assertTrue(applyResult is OverlayApplyRequestResult.Success)
        val applyRequest = (applyResult as OverlayApplyRequestResult.Success).request

        val cache = FileOverlayCache(Files.createTempDirectory("msp-integration-cache").toFile())
        cache.save(
            CachedOverlay(
                overlayId = applyRequest.overlayId,
                code = applyRequest.code,
                overlayJson = applyRequest.overlayJson,
            ),
        )

        val cached = cache.loadLastApplied()
        val document = OverlayJsonParser().parse(cached!!.overlayJson)
        val plan = OverlayRenderPlanner().plan(document, targetWidth = 960, targetHeight = 540)

        assertEquals("ovl_android_001", cached.overlayId)
        assertEquals("ABC123", cached.code)
        assertEquals(1, plan.commands.size)
    }

    private fun overlayJson(): String {
        return """
            {
              "schemaVersion":"1.0.0",
              "overlayId":"ovl_android_001",
              "name":"Android Overlay",
              "platform":"android",
              "canvas":{"baseWidth":1920,"baseHeight":1080},
              "overlaySettings":{"opacity":1},
              "elements":[
                {
                  "id":"rect-1",
                  "type":"rect",
                  "x":100,
                  "y":100,
                  "width":200,
                  "height":80,
                  "opacity":1,
                  "zIndex":1,
                  "visible":true,
                  "locked":false,
                  "fillColor":"#2563EB",
                  "strokeColor":"#0F172A",
                  "strokeWidth":2,
                  "cornerRadius":8
                }
              ],
              "meta":{"createdAt":null,"updatedAt":null}
            }
        """.trimIndent()
    }

    private fun jsonString(value: String): String {
        return buildString {
            append('"')
            value.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }
    }

    private class StaticTokenProvider(private val token: String?) : AuthTokenProvider {
        override fun getAccessToken(): String? = token
    }
}

