package com.mspoverlay.android.overlay.apply

import com.mspoverlay.android.feature.discover.data.OverlayCodeLoadDto
import com.mspoverlay.android.overlay.parser.OverlayJsonParser

data class OverlayApplyRequest(
    val overlayId: String,
    val code: String?,
    val overlayJson: String,
)

sealed class OverlayApplyRequestResult {
    data class Success(val request: OverlayApplyRequest) : OverlayApplyRequestResult()
    data class Failure(val reason: String) : OverlayApplyRequestResult()
}

class OverlayApplyRequestFactory(
    private val parser: OverlayJsonParser = OverlayJsonParser(),
) {
    fun fromCodeLoad(response: OverlayCodeLoadDto): OverlayApplyRequestResult {
        val overlayJson = response.overlayJson
        if (overlayJson.isNullOrBlank()) {
            return OverlayApplyRequestResult.Failure("missing_overlay_json")
        }

        val parsed = runCatching { parser.parse(overlayJson) }
            .getOrElse { return OverlayApplyRequestResult.Failure(it.message ?: "invalid_overlay_json") }

        if (parsed.overlayId != response.overlayId) {
            return OverlayApplyRequestResult.Failure("overlay_id_mismatch")
        }

        return OverlayApplyRequestResult.Success(
            OverlayApplyRequest(
                overlayId = response.overlayId,
                code = response.code,
                overlayJson = overlayJson,
            ),
        )
    }
}

