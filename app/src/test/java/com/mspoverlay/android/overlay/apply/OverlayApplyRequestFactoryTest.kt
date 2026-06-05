package com.mspoverlay.android.overlay.apply

import com.mspoverlay.android.feature.discover.data.OverlayCodeLoadDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayApplyRequestFactoryTest {
    private val factory = OverlayApplyRequestFactory()

    @Test
    fun createsApplyRequestFromValidCodeLoadResponse() {
        val result = factory.fromCodeLoad(
            OverlayCodeLoadDto(
                id = 1,
                overlayId = "ovl_android_001",
                code = "ABC123",
                name = "Android Overlay",
                description = "",
                platform = "android",
                thumbnailUrl = null,
                schemaVersion = "1.0.0",
                overlayJson = validOverlayJson("ovl_android_001"),
                createdAt = null,
                updatedAt = null,
            ),
        )

        assertTrue(result is OverlayApplyRequestResult.Success)
        result as OverlayApplyRequestResult.Success
        assertEquals("ovl_android_001", result.request.overlayId)
        assertEquals("ABC123", result.request.code)
    }

    @Test
    fun rejectsMissingOverlayJson() {
        val result = factory.fromCodeLoad(
            OverlayCodeLoadDto(
                id = 1,
                overlayId = "ovl_android_001",
                code = "ABC123",
                name = "Android Overlay",
                description = "",
                platform = "android",
                thumbnailUrl = null,
                schemaVersion = "1.0.0",
                overlayJson = null,
                createdAt = null,
                updatedAt = null,
            ),
        )

        assertEquals(OverlayApplyRequestResult.Failure("missing_overlay_json"), result)
    }

    @Test
    fun rejectsMismatchedOverlayId() {
        val result = factory.fromCodeLoad(
            OverlayCodeLoadDto(
                id = 1,
                overlayId = "ovl_android_001",
                code = "ABC123",
                name = "Android Overlay",
                description = "",
                platform = "android",
                thumbnailUrl = null,
                schemaVersion = "1.0.0",
                overlayJson = validOverlayJson("ovl_other"),
                createdAt = null,
                updatedAt = null,
            ),
        )

        assertEquals(OverlayApplyRequestResult.Failure("overlay_id_mismatch"), result)
    }

    private fun validOverlayJson(overlayId: String): String {
        return """
            {
              "schemaVersion":"1.0.0",
              "overlayId":"$overlayId",
              "name":"Android Overlay",
              "platform":"android",
              "canvas":{"baseWidth":1920,"baseHeight":1080},
              "overlaySettings":{"opacity":1},
              "elements":[],
              "meta":{"createdAt":null,"updatedAt":null}
            }
        """.trimIndent()
    }
}
