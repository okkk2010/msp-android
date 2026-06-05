package com.mspoverlay.android.overlay.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayPermissionPolicyTest {
    @Test
    fun blocksStartWhenOverlayPermissionIsMissing() {
        val decision = OverlayPermissionPolicy.evaluate(
            OverlayPermissionState(
                canDrawOverlays = false,
                notificationPermissionGranted = true,
            ),
        )

        assertFalse(decision.canStart)
        assertEquals("overlay_permission_required", decision.reason)
    }

    @Test
    fun blocksStartWhenNotificationPermissionIsMissing() {
        val decision = OverlayPermissionPolicy.evaluate(
            OverlayPermissionState(
                canDrawOverlays = true,
                notificationPermissionGranted = false,
            ),
        )

        assertFalse(decision.canStart)
        assertEquals("notification_permission_required", decision.reason)
    }

    @Test
    fun allowsStartWhenRequiredPermissionsAreGranted() {
        val decision = OverlayPermissionPolicy.evaluate(
            OverlayPermissionState(
                canDrawOverlays = true,
                notificationPermissionGranted = true,
            ),
        )

        assertTrue(decision.canStart)
        assertNull(decision.reason)
    }
}
