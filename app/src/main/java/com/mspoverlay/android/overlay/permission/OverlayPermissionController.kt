package com.mspoverlay.android.overlay.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.content.pm.PackageManager

class OverlayPermissionController(
    private val context: Context,
) {
    fun currentState(): OverlayPermissionState {
        return OverlayPermissionState(
            canDrawOverlays = Settings.canDrawOverlays(context),
            notificationPermissionGranted = hasNotificationPermission(),
        )
    }

    fun createOverlaySettingsIntent(): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}"),
        )
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
}

