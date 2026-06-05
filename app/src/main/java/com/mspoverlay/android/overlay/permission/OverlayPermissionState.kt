package com.mspoverlay.android.overlay.permission

data class OverlayPermissionState(
    val canDrawOverlays: Boolean,
    val notificationPermissionGranted: Boolean,
)

data class OverlayStartDecision(
    val canStart: Boolean,
    val reason: String?,
)

object OverlayPermissionPolicy {
    fun evaluate(state: OverlayPermissionState): OverlayStartDecision {
        if (!state.canDrawOverlays) {
            return OverlayStartDecision(
                canStart = false,
                reason = "overlay_permission_required",
            )
        }
        if (!state.notificationPermissionGranted) {
            return OverlayStartDecision(
                canStart = false,
                reason = "notification_permission_required",
            )
        }
        return OverlayStartDecision(canStart = true, reason = null)
    }
}

