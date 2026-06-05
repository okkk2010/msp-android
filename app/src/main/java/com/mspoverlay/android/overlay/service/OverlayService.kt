package com.mspoverlay.android.overlay.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.mspoverlay.android.MainActivity
import com.mspoverlay.android.R
import com.mspoverlay.android.overlay.parser.OverlayJsonParser

class OverlayService : Service() {
    private lateinit var windowController: OverlayWindowController
    private val parser = OverlayJsonParser()

    override fun onCreate() {
        super.onCreate()
        windowController = OverlayWindowController(this)
        ensureNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                val document = intent?.getStringExtra(EXTRA_OVERLAY_JSON)
                    ?.takeIf { it.isNotBlank() }
                    ?.let { runCatching { parser.parse(it) }.getOrNull() }
                windowController.show(document)
                return START_STICKY
            }
        }
    }

    override fun onDestroy() {
        windowController.hide()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.overlay_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        )
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, OverlayService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setContentTitle(getString(R.string.overlay_notification_title))
            .setContentText(getString(R.string.overlay_notification_text))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(openIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.overlay_notification_stop),
                stopIntent,
            )
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START = "com.mspoverlay.android.overlay.action.START"
        const val ACTION_STOP = "com.mspoverlay.android.overlay.action.STOP"
        const val EXTRA_OVERLAY_JSON = "com.mspoverlay.android.overlay.extra.OVERLAY_JSON"
        private const val CHANNEL_ID = "msp_overlay"
        private const val NOTIFICATION_ID = 2010

        fun createStartIntent(context: Context, overlayJson: String): Intent {
            return Intent(context, OverlayService::class.java)
                .setAction(ACTION_START)
                .putExtra(EXTRA_OVERLAY_JSON, overlayJson)
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, OverlayService::class.java)
                .setAction(ACTION_STOP)
        }
    }
}
