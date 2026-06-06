package com.mspoverlay.android

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.mspoverlay.android.core.config.AppEnvironment
import com.mspoverlay.android.overlay.permission.OverlayPermissionController
import com.mspoverlay.android.overlay.permission.OverlayPermissionPolicy
import com.mspoverlay.android.overlay.service.OverlayService

class MainActivity : Activity() {
    private lateinit var permissionController: OverlayPermissionController
    private lateinit var contentContainer: FrameLayout
    private lateinit var screenTitle: TextView
    private lateinit var backButton: Button
    private var currentScreen: Screen = Screen.Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionController = OverlayPermissionController(this)
        contentContainer = findViewById(R.id.contentContainer)
        screenTitle = findViewById(R.id.screenTitle)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { showScreen(Screen.Home) }

        showScreen(Screen.Home)
    }

    override fun onResume() {
        super.onResume()
        if (currentScreen == Screen.Home || currentScreen == Screen.PermissionGuide) {
            bindCurrentScreen()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (currentScreen == Screen.Home) {
            super.onBackPressed()
        } else {
            showScreen(Screen.Home)
        }
    }

    private fun showScreen(screen: Screen) {
        currentScreen = screen
        screenTitle.setText(screen.titleRes)
        backButton.visibility = if (screen == Screen.Home) View.INVISIBLE else View.VISIBLE

        contentContainer.removeAllViews()
        LayoutInflater.from(this).inflate(screen.layoutRes, contentContainer, true)
        bindCurrentScreen()
    }

    private fun bindCurrentScreen() {
        when (currentScreen) {
            Screen.Home -> bindHomeScreen()
            Screen.CodeLoad -> bindCodeLoadScreen()
            Screen.Discover -> bindDiscoverScreen()
            Screen.Detail -> bindDetailScreen()
            Screen.Library -> bindLibraryScreen()
            Screen.Settings -> bindSettingsScreen()
            Screen.PermissionGuide -> bindPermissionGuideScreen()
        }
    }

    private fun bindHomeScreen() {
        updatePermissionStatus(
            overlayStatusView = findViewById(R.id.overlayPermissionStatus),
            notificationStatusView = findViewById(R.id.notificationPermissionStatus),
        )

        findViewById<Button>(R.id.startOverlayButton).setOnClickListener {
            handleStartOverlay()
        }
        findViewById<Button>(R.id.stopOverlayButton).setOnClickListener {
            startService(OverlayService.createStopIntent(this))
            Toast.makeText(this, R.string.message_overlay_stop_requested, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.openPermissionSettingsButton).setOnClickListener {
            startActivity(permissionController.createOverlaySettingsIntent())
        }
        findViewById<Button>(R.id.codeLoadButton).setOnClickListener { showScreen(Screen.CodeLoad) }
        findViewById<Button>(R.id.discoverButton).setOnClickListener { showScreen(Screen.Discover) }
        findViewById<Button>(R.id.libraryButton).setOnClickListener { showScreen(Screen.Library) }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { showScreen(Screen.Settings) }
        findViewById<Button>(R.id.permissionGuideButton).setOnClickListener {
            showScreen(Screen.PermissionGuide)
        }
    }

    private fun bindCodeLoadScreen() {
        findViewById<Button>(R.id.loadCodeButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.applyCodeOverlayButton).setOnClickListener {
            handleStartOverlay()
        }
    }

    private fun bindDiscoverScreen() {
        findViewById<Button>(R.id.discoverRefreshButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.discoverDetailButton).setOnClickListener {
            showScreen(Screen.Detail)
        }
        findViewById<Button>(R.id.discoverApplyButton).setOnClickListener {
            handleStartOverlay()
        }
    }

    private fun bindDetailScreen() {
        findViewById<Button>(R.id.detailApplyButton).setOnClickListener {
            handleStartOverlay()
        }
        findViewById<Button>(R.id.detailSaveButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindLibraryScreen() {
        findViewById<Button>(R.id.libraryLoginButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindSettingsScreen() {
        findViewById<TextView>(R.id.settingsApiBaseUrl).text = getString(
            R.string.settings_api_base_url,
            AppEnvironment.apiBaseUrl,
        )
        findViewById<Button>(R.id.settingsLoginButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.settingsLogoutButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.settingsOverlayPermissionButton).setOnClickListener {
            startActivity(permissionController.createOverlaySettingsIntent())
        }
        findViewById<Button>(R.id.settingsNotificationPermissionButton).setOnClickListener {
            requestNotificationPermission()
        }
        findViewById<Button>(R.id.settingsClearCacheButton).setOnClickListener {
            Toast.makeText(this, R.string.message_feature_pending, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindPermissionGuideScreen() {
        updatePermissionStatus(
            overlayStatusView = findViewById(R.id.permissionGuideState),
            notificationStatusView = null,
        )
        findViewById<Button>(R.id.permissionGuideSettingsButton).setOnClickListener {
            startActivity(permissionController.createOverlaySettingsIntent())
        }
        findViewById<Button>(R.id.permissionGuideNotificationButton).setOnClickListener {
            requestNotificationPermission()
        }
    }

    private fun handleStartOverlay() {
        val decision = OverlayPermissionPolicy.evaluate(permissionController.currentState())
        if (!decision.canStart) {
            val message = when (decision.reason) {
                "overlay_permission_required" -> R.string.message_overlay_permission_required
                "notification_permission_required" -> R.string.message_notification_permission_required
                else -> R.string.message_overlay_not_loaded
            }
            if (decision.reason == "notification_permission_required") {
                requestNotificationPermission()
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, R.string.message_overlay_not_loaded, Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
        }
    }

    private fun updatePermissionStatus(
        overlayStatusView: TextView,
        notificationStatusView: TextView?,
    ) {
        val state = permissionController.currentState()
        overlayStatusView.setText(
            if (state.canDrawOverlays) {
                R.string.status_overlay_permission_allowed
            } else {
                R.string.status_overlay_permission_required
            },
        )
        notificationStatusView?.setText(
            if (state.notificationPermissionGranted) {
                R.string.status_notification_permission_allowed
            } else {
                R.string.status_notification_permission_required
            },
        )
    }

    private enum class Screen(
        val layoutRes: Int,
        val titleRes: Int,
    ) {
        Home(R.layout.screen_home, R.string.app_name),
        CodeLoad(R.layout.screen_code_load, R.string.nav_code_load),
        Discover(R.layout.screen_discover, R.string.nav_discover),
        Detail(R.layout.screen_detail, R.string.screen_overlay_detail),
        Library(R.layout.screen_library, R.string.nav_library),
        Settings(R.layout.screen_settings, R.string.nav_settings),
        PermissionGuide(R.layout.screen_permission_guide, R.string.nav_permission_guide),
    }

    private companion object {
        const val REQUEST_NOTIFICATIONS = 1001
    }
}
