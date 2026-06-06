package com.mspoverlay.android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.mspoverlay.android.core.auth.AuthTokens
import com.mspoverlay.android.core.auth.EncryptedPreferencesTokenStore
import com.mspoverlay.android.core.auth.StoredAuthTokenProvider
import com.mspoverlay.android.core.auth.TokenStore
import com.mspoverlay.android.core.auth.oauth.AndroidOAuthUrlBuilder
import com.mspoverlay.android.core.auth.oauth.OAuthCallbackParser
import com.mspoverlay.android.core.auth.oauth.OAuthCallbackResult
import com.mspoverlay.android.core.auth.oauth.OAuthStateGenerator
import com.mspoverlay.android.core.config.AppEnvironment
import com.mspoverlay.android.core.network.ApiClientFactory
import com.mspoverlay.android.feature.discover.data.GameDto
import com.mspoverlay.android.feature.discover.data.OverlayCodeLoadDto
import com.mspoverlay.android.feature.discover.data.OverlayDetailDto
import com.mspoverlay.android.feature.discover.data.OverlaySummaryDto
import com.mspoverlay.android.feature.discover.data.PublicMspApi
import com.mspoverlay.android.feature.library.data.AuthenticatedMspApi
import com.mspoverlay.android.feature.library.data.LibraryItemDto
import com.mspoverlay.android.feature.library.data.LibrarySaveRequestDto
import com.mspoverlay.android.overlay.apply.OverlayApplyRequestFactory
import com.mspoverlay.android.overlay.apply.OverlayApplyRequestResult
import com.mspoverlay.android.overlay.cache.CachedOverlay
import com.mspoverlay.android.overlay.cache.FileOverlayCache
import com.mspoverlay.android.overlay.cache.OverlayCache
import com.mspoverlay.android.overlay.parser.OverlayJsonParser
import com.mspoverlay.android.overlay.permission.OverlayPermissionController
import com.mspoverlay.android.overlay.permission.OverlayPermissionPolicy
import com.mspoverlay.android.overlay.render.OverlayPreviewView
import com.mspoverlay.android.overlay.service.OverlayService
import kotlinx.coroutines.runBlocking
import java.io.File

class MainActivity : Activity() {
    private lateinit var permissionController: OverlayPermissionController
    private lateinit var tokenStore: TokenStore
    private lateinit var overlayCache: OverlayCache
    private lateinit var publicApi: PublicMspApi
    private lateinit var authenticatedApi: AuthenticatedMspApi
    private lateinit var contentContainer: FrameLayout
    private lateinit var screenTitle: TextView
    private lateinit var backButton: Button

    private val parser = OverlayJsonParser()
    private val applyRequestFactory = OverlayApplyRequestFactory(parser)
    private val oauthStateGenerator = OAuthStateGenerator()
    private val oauthCallbackParser = OAuthCallbackParser()
    private var currentScreen: Screen = Screen.Home
    private var currentUserName: String? = null
    private var currentLoadedOverlay: LoadedOverlay? = null
    private var selectedSummary: OverlaySummaryDto? = null
    private var selectedDetail: OverlayDetailDto? = null
    private var discoverGames: List<GameDto> = emptyList()
    private var libraryItems: List<LibraryItemDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionController = OverlayPermissionController(this)
        tokenStore = EncryptedPreferencesTokenStore(this)
        overlayCache = FileOverlayCache(File(filesDir, "overlay-cache"))
        val apiFactory = ApiClientFactory(
            baseUrl = AppEnvironment.apiBaseUrl,
            tokenProvider = StoredAuthTokenProvider(tokenStore),
            enableHttpLogging = AppEnvironment.httpLoggingEnabled,
        )
        publicApi = apiFactory.createPublicRetrofit().create(PublicMspApi::class.java)
        authenticatedApi = apiFactory.createAuthenticatedRetrofit().create(AuthenticatedMspApi::class.java)
        contentContainer = findViewById(R.id.contentContainer)
        screenTitle = findViewById(R.id.screenTitle)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { showScreen(Screen.Home) }

        restoreCachedOverlay()
        handleOAuthCallback(intent)
        showScreen(Screen.Home)
        refreshMe(silent = true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            setIntent(intent)
            handleOAuthCallback(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (currentScreen == Screen.Home || currentScreen == Screen.PermissionGuide) {
            bindCurrentScreen()
        }
    }

    @Deprecated("Deprecated in the Android framework; kept while this project avoids AndroidX back handling.")
    override fun onBackPressed() {
        if (currentScreen == Screen.Home) {
            @Suppress("DEPRECATION")
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
        findViewById<TextView>(R.id.loginStatus).text = loginStatusText()
        findViewById<TextView>(R.id.currentOverlayStatus).text = overlayStatusText()
        findViewById<Button>(R.id.startOverlayButton).setOnClickListener { applyCurrentLoadedOverlay() }
        findViewById<Button>(R.id.stopOverlayButton).setOnClickListener {
            startService(OverlayService.createStopIntent(this))
            toast(R.string.message_overlay_stop_requested)
        }
        findViewById<Button>(R.id.openPermissionSettingsButton).setOnClickListener {
            startActivity(permissionController.createOverlaySettingsIntent())
        }
        findViewById<Button>(R.id.codeLoadButton).setOnClickListener { showScreen(Screen.CodeLoad) }
        findViewById<Button>(R.id.discoverButton).setOnClickListener { showScreen(Screen.Discover) }
        findViewById<Button>(R.id.libraryButton).setOnClickListener { showScreen(Screen.Library) }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { showScreen(Screen.Settings) }
        findViewById<Button>(R.id.permissionGuideButton).setOnClickListener { showScreen(Screen.PermissionGuide) }
    }

    private fun bindCodeLoadScreen() {
        val codeInput = findViewById<EditText>(R.id.codeInput)
        val stateView = findViewById<TextView>(R.id.codeLoadState)
        val preview = findViewById<OverlayPreviewView>(R.id.codeLoadPreview)
        val applyButton = findViewById<Button>(R.id.applyCodeOverlayButton)
        currentLoadedOverlay?.let {
            codeInput.setText(it.code.orEmpty())
            stateView.text = loadedOverlayDescription(it)
            preview.setOverlayDocument(runCatching { parser.parse(it.overlayJson) }.getOrNull())
            applyButton.isEnabled = true
        }
        findViewById<Button>(R.id.loadCodeButton).setOnClickListener {
            val code = codeInput.text.toString().trim().uppercase()
            if (!CODE_PATTERN.matches(code)) {
                toast(R.string.message_invalid_code)
                return@setOnClickListener
            }
            loadOverlayByCode(code, stateView, preview, applyButton)
        }
        applyButton.setOnClickListener { applyCurrentLoadedOverlay() }
    }

    private fun bindDiscoverScreen() {
        val gameSpinner = findViewById<Spinner>(R.id.gameFilterSpinner)
        val sortSpinner = findViewById<Spinner>(R.id.sortSpinner)
        val searchInput = findViewById<EditText>(R.id.discoverSearchInput)
        setSpinnerItems(gameSpinner, listOf("전체 게임"))
        setSpinnerItems(sortSpinner, listOf("최신순", "이름순"))
        if (discoverGames.isEmpty()) {
            loadGames(gameSpinner)
        }
        findViewById<Button>(R.id.discoverRefreshButton).setOnClickListener {
            val game = discoverGames.getOrNull(gameSpinner.selectedItemPosition - 1)?.slug
            val sort = if (sortSpinner.selectedItemPosition == 1) "name" else "latest"
            loadDiscoverOverlays(searchInput.text.toString().trim(), game, sort)
        }
        findViewById<Button>(R.id.discoverDetailButton).setOnClickListener {
            selectedSummary?.let { showDetailFromSummary(it) } ?: toast(R.string.message_no_data)
        }
        findViewById<Button>(R.id.discoverApplyButton).setOnClickListener { applyCurrentLoadedOverlay() }
    }

    private fun bindDetailScreen() {
        renderDetail()
        findViewById<Button>(R.id.detailApplyButton).setOnClickListener { applyCurrentLoadedOverlay() }
        findViewById<Button>(R.id.detailSaveButton).setOnClickListener {
            val overlayId = selectedDetail?.id ?: selectedSummary?.id ?: currentLoadedOverlay?.databaseId
            if (overlayId == null) {
                toast(R.string.message_no_data)
            } else {
                saveToLibrary(overlayId)
            }
        }
    }

    private fun bindLibraryScreen() {
        findViewById<TextView>(R.id.libraryAuthState).text = loginStatusText()
        findViewById<Button>(R.id.libraryLoginButton).setOnClickListener { startGoogleLogin() }
        val searchInput = findViewById<EditText>(R.id.librarySearchInput)
        searchInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    renderLibraryItems(s?.toString().orEmpty())
                }
                override fun afterTextChanged(s: Editable?) = Unit
            },
        )
        loadLibrary()
    }

    private fun bindSettingsScreen() {
        findViewById<TextView>(R.id.settingsAccountState).text = loginStatusText()
        findViewById<TextView>(R.id.settingsApiBaseUrl).text = getString(
            R.string.settings_api_base_url,
            AppEnvironment.apiBaseUrl,
        )
        findViewById<Button>(R.id.settingsLoginButton).setOnClickListener { startGoogleLogin() }
        findViewById<Button>(R.id.settingsLogoutButton).apply {
            isEnabled = tokenStore.getAccessToken() != null
            setOnClickListener { logout() }
        }
        findViewById<Button>(R.id.settingsOverlayPermissionButton).setOnClickListener {
            startActivity(permissionController.createOverlaySettingsIntent())
        }
        findViewById<Button>(R.id.settingsNotificationPermissionButton).setOnClickListener {
            requestNotificationPermission()
        }
        findViewById<Button>(R.id.settingsClearCacheButton).apply {
            isEnabled = currentLoadedOverlay != null
            setOnClickListener {
                overlayCache.clear()
                currentLoadedOverlay = null
                toast(R.string.message_cache_cleared)
                bindSettingsScreen()
            }
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

    private fun loadOverlayByCode(
        code: String,
        stateView: TextView?,
        preview: OverlayPreviewView?,
        applyButton: Button?,
    ) {
        stateView?.setText(R.string.message_loading)
        runIo {
            val response = publicApi.getOverlayByCode(code)
            val dto = response.data
            if (!response.success || dto == null) {
                onUi { stateView?.text = response.message ?: getString(R.string.message_no_data) }
                return@runIo
            }

            val result = applyRequestFactory.fromCodeLoad(dto)
            if (result !is OverlayApplyRequestResult.Success) {
                val reason = (result as OverlayApplyRequestResult.Failure).reason
                onUi { stateView?.text = reason }
                return@runIo
            }

            val document = parser.parse(result.request.overlayJson)
            val loaded = LoadedOverlay(
                databaseId = dto.id,
                overlayId = result.request.overlayId,
                code = result.request.code,
                name = dto.name ?: dto.overlayId,
                description = dto.description,
                platform = dto.platform,
                overlayJson = result.request.overlayJson,
            )
            onUi {
                currentLoadedOverlay = loaded
                selectedSummary = null
                selectedDetail = null
                stateView?.text = loadedOverlayDescription(loaded)
                preview?.setOverlayDocument(document)
                applyButton?.isEnabled = true
                toast(R.string.message_overlay_loaded)
            }
        }
    }

    private fun loadGames(spinner: Spinner) {
        runIo {
            val response = publicApi.getGames("android")
            val games = response.data.orEmpty()
            onUi {
                discoverGames = games
                setSpinnerItems(spinner, listOf("전체 게임") + games.map { it.displayName ?: it.name ?: it.slug.orEmpty() })
            }
        }
    }

    private fun loadDiscoverOverlays(keyword: String, game: String?, sort: String) {
        val stateView = findViewById<TextView>(R.id.discoverState)
        stateView.setText(R.string.message_loading)
        runIo {
            val response = publicApi.getOverlays(
                page = 0,
                size = 20,
                keyword = keyword.takeIf { it.isNotBlank() },
                platform = "android",
                game = game,
                sort = sort,
            )
            val items = response.data?.content.orEmpty()
            onUi {
                stateView.text = if (items.isEmpty()) {
                    getString(R.string.message_no_data)
                } else {
                    "총 ${items.size}개 오버레이"
                }
                renderDiscoverItems(items)
            }
        }
    }

    private fun renderDiscoverItems(items: List<OverlaySummaryDto>) {
        val list = findViewById<LinearLayout>(R.id.discoverList)
        list.removeAllViews()
        if (items.isEmpty()) {
            list.addView(label(getString(R.string.message_no_data)))
            return
        }
        items.forEach { item ->
            val card = cardContainer()
            card.addView(title(item.name ?: item.overlayId))
            card.addView(label("${item.platform ?: "-"} · ${item.game ?: "-"} · 코드 ${item.code ?: "-"}"))
            val buttons = horizontalRow()
            buttons.addView(actionButton(getString(R.string.action_view_detail)) {
                showDetailFromSummary(item)
            })
            buttons.addView(actionButton(getString(R.string.action_apply_overlay)) {
                val code = item.code
                if (code.isNullOrBlank()) {
                    toast(R.string.message_no_data)
                } else {
                    showScreen(Screen.CodeLoad)
                    val state = findViewById<TextView>(R.id.codeLoadState)
                    val preview = findViewById<OverlayPreviewView>(R.id.codeLoadPreview)
                    val apply = findViewById<Button>(R.id.applyCodeOverlayButton)
                    findViewById<EditText>(R.id.codeInput).setText(code)
                    loadOverlayByCode(code, state, preview, apply)
                }
            })
            card.addView(buttons)
            list.addView(card)
        }
    }

    private fun showDetailFromSummary(summary: OverlaySummaryDto) {
        selectedSummary = summary
        selectedDetail = null
        showScreen(Screen.Detail)
        runIo {
            val response = publicApi.getOverlayDetail(summary.overlayId)
            onUi {
                if (response.success && response.data != null) {
                    selectedDetail = response.data
                }
                renderDetail()
            }
        }
    }

    private fun renderDetail() {
        val summary = selectedSummary
        val detail = selectedDetail
        val loaded = currentLoadedOverlay
        findViewById<TextView>(R.id.detailName).text =
            detail?.name ?: summary?.name ?: loaded?.name ?: getString(R.string.placeholder_overlay_card_title)
        findViewById<TextView>(R.id.detailDescription).text =
            detail?.description ?: summary?.description ?: loaded?.description ?: getString(R.string.detail_placeholder_description)
        findViewById<TextView>(R.id.detailMeta).text = buildString {
            appendLine("코드: ${detail?.code ?: summary?.code ?: loaded?.code ?: "-"}")
            appendLine("플랫폼: ${detail?.platform ?: summary?.platform ?: loaded?.platform ?: "-"}")
            appendLine("게임: ${detail?.game ?: summary?.game ?: "-"}")
            appendLine("캔버스: ${detail?.canvasBaseWidth ?: "-"} x ${detail?.canvasBaseHeight ?: "-"}")
            append("작성자: ${detail?.author?.name ?: summary?.authorName ?: "-"}")
        }
        val preview = findViewById<OverlayPreviewView>(R.id.detailPreview)
        preview.setOverlayDocument(loaded?.let { runCatching { parser.parse(it.overlayJson) }.getOrNull() })
        findViewById<Button>(R.id.detailApplyButton).isEnabled = loaded != null
    }

    private fun loadLibrary() {
        val state = findViewById<TextView>(R.id.libraryState)
        if (tokenStore.getAccessToken().isNullOrBlank()) {
            state.setText(R.string.library_login_required)
            renderLibraryItems("")
            return
        }
        state.setText(R.string.message_loading)
        runIo {
            val response = authenticatedApi.getLibrary()
            onUi {
                libraryItems = response.data.orEmpty()
                state.text = if (libraryItems.isEmpty()) getString(R.string.message_no_data) else "총 ${libraryItems.size}개 저장됨"
                renderLibraryItems(findViewById<EditText>(R.id.librarySearchInput).text.toString())
            }
        }
    }

    private fun renderLibraryItems(keyword: String) {
        val list = findViewById<LinearLayout>(R.id.libraryList)
        list.removeAllViews()
        val filtered = libraryItems.filter {
            val text = listOfNotNull(it.overlay.name, it.overlay.description, it.overlay.code, it.overlay.game)
                .joinToString(" ")
            text.contains(keyword, ignoreCase = true)
        }
        if (filtered.isEmpty()) {
            list.addView(label(getString(R.string.message_no_data)))
            return
        }
        filtered.forEach { item ->
            val card = cardContainer()
            card.addView(title(item.overlay.name ?: item.overlay.overlayId))
            card.addView(label("${item.overlay.platform ?: "-"} · ${item.overlay.game ?: "-"} · 코드 ${item.overlay.code ?: "-"}"))
            val buttons = horizontalRow()
            buttons.addView(actionButton(getString(R.string.action_apply_overlay)) {
                val code = item.overlay.code
                if (code.isNullOrBlank()) {
                    toast(R.string.message_no_data)
                } else {
                    showScreen(Screen.CodeLoad)
                    loadOverlayByCode(
                        code,
                        findViewById(R.id.codeLoadState),
                        findViewById(R.id.codeLoadPreview),
                        findViewById(R.id.applyCodeOverlayButton),
                    )
                }
            })
            buttons.addView(actionButton(getString(R.string.action_remove)) {
                deleteFromLibrary(item.overlay.id)
            })
            card.addView(buttons)
            list.addView(card)
        }
    }

    private fun saveToLibrary(overlayId: Long) {
        if (tokenStore.getAccessToken().isNullOrBlank()) {
            startGoogleLogin()
            return
        }
        runIo {
            authenticatedApi.saveToLibrary(LibrarySaveRequestDto(overlayId))
            onUi { toast(R.string.message_overlay_saved) }
        }
    }

    private fun deleteFromLibrary(overlayId: Long) {
        runIo {
            authenticatedApi.deleteFromLibrary(overlayId)
            onUi {
                toast(R.string.message_overlay_removed)
                loadLibrary()
            }
        }
    }

    private fun applyCurrentLoadedOverlay() {
        val loaded = currentLoadedOverlay
        if (loaded == null) {
            toast(R.string.message_overlay_not_loaded)
            return
        }
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
            toast(message)
            return
        }
        overlayCache.save(CachedOverlay(loaded.overlayId, loaded.code, loaded.overlayJson))
        val intent = OverlayService.createStartIntent(this, loaded.overlayJson)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        toast(R.string.message_overlay_applied)
    }

    private fun startGoogleLogin() {
        val state = oauthStateGenerator.generate()
        oauthPreferences().edit().putString(KEY_OAUTH_STATE, state).apply()
        val url = AndroidOAuthUrlBuilder(AppEnvironment.apiBaseUrl).buildGoogleStartUrl(state)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        toast(R.string.message_login_started)
    }

    private fun handleOAuthCallback(intent: Intent) {
        val callbackUrl = intent.data?.toString() ?: return
        val expectedState = oauthPreferences().getString(KEY_OAUTH_STATE, null).orEmpty()
        when (val result = oauthCallbackParser.parse(callbackUrl, expectedState)) {
            is OAuthCallbackResult.Success -> {
                tokenStore.save(result.tokens)
                oauthPreferences().edit().remove(KEY_OAUTH_STATE).apply()
                toast(R.string.message_login_success)
                refreshMe(silent = true)
            }
            is OAuthCallbackResult.Failure -> {
                toast(getString(R.string.message_login_failed, result.reason))
            }
        }
    }

    private fun refreshMe(silent: Boolean) {
        if (tokenStore.getAccessToken().isNullOrBlank()) {
            currentUserName = null
            return
        }
        runIo {
            val response = authenticatedApi.getMe()
            onUi {
                currentUserName = response.data?.name ?: response.data?.email
                if (!silent && currentUserName == null) {
                    toast(R.string.message_no_data)
                }
                if (currentScreen == Screen.Home || currentScreen == Screen.Settings || currentScreen == Screen.Library) {
                    bindCurrentScreen()
                }
            }
        }
    }

    private fun logout() {
        runIo {
            runCatching { authenticatedApi.logout() }
            onUi {
                tokenStore.clear()
                currentUserName = null
                libraryItems = emptyList()
                toast(R.string.message_logout_success)
                bindSettingsScreen()
            }
        }
    }

    private fun restoreCachedOverlay() {
        val cached = overlayCache.loadLastApplied() ?: return
        currentLoadedOverlay = LoadedOverlay(
            databaseId = null,
            overlayId = cached.overlayId,
            code = cached.code,
            name = cached.code ?: cached.overlayId,
            description = null,
            platform = "android",
            overlayJson = cached.overlayJson,
        )
    }

    private fun updatePermissionStatus(overlayStatusView: TextView, notificationStatusView: TextView?) {
        val state = permissionController.currentState()
        overlayStatusView.setText(
            if (state.canDrawOverlays) R.string.status_overlay_permission_allowed else R.string.status_overlay_permission_required,
        )
        notificationStatusView?.setText(
            if (state.notificationPermissionGranted) R.string.status_notification_permission_allowed else R.string.status_notification_permission_required,
        )
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
        }
    }

    private fun setSpinnerItems(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun cardContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFFFFFFFF.toInt())
            elevation = dp(1).toFloat()
            setPadding(dp(16), dp(16), dp(16), dp(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                bottomMargin = dp(12)
            }
        }
    }

    private fun horizontalRow(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dp(12)
            }
        }
    }

    private fun actionButton(text: String, action: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setOnClickListener { action() }
            layoutParams = LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                marginStart = dp(6)
                marginEnd = dp(6)
            }
        }
    }

    private fun title(text: String): TextView {
        return label(text).apply {
            textSize = 17f
            setTextColor(0xFF0F172A.toInt())
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
    }

    private fun label(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(0xFF334155.toInt())
        }
    }

    private fun loadedOverlayDescription(loaded: LoadedOverlay): String {
        return "${loaded.name}\n코드: ${loaded.code ?: "-"} · 플랫폼: ${loaded.platform ?: "-"}"
    }

    private fun loginStatusText(): String {
        return currentUserName?.let { getString(R.string.status_login_user, it) }
            ?: getString(R.string.status_login_unknown)
    }

    private fun overlayStatusText(): String {
        return currentLoadedOverlay?.let { getString(R.string.status_overlay_loaded, it.name) }
            ?: getString(R.string.status_overlay_none)
    }

    private fun runIo(block: suspend () -> Unit) {
        Thread {
            runCatching {
                runBlocking { block() }
            }.onFailure { error ->
                onUi { toast(getString(R.string.message_network_error, error.message ?: error.javaClass.simpleName)) }
            }
        }.start()
    }

    private fun onUi(action: () -> Unit) {
        runOnUiThread(action)
    }

    private fun toast(messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun oauthPreferences() = getSharedPreferences("msp_oauth", MODE_PRIVATE)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class LoadedOverlay(
        val databaseId: Long?,
        val overlayId: String,
        val code: String?,
        val name: String,
        val description: String?,
        val platform: String?,
        val overlayJson: String,
    )

    private enum class Screen(val layoutRes: Int, val titleRes: Int) {
        Home(R.layout.screen_home, R.string.app_name),
        CodeLoad(R.layout.screen_code_load, R.string.nav_code_load),
        Discover(R.layout.screen_discover, R.string.nav_discover),
        Detail(R.layout.screen_detail, R.string.screen_overlay_detail),
        Library(R.layout.screen_library, R.string.nav_library),
        Settings(R.layout.screen_settings, R.string.nav_settings),
        PermissionGuide(R.layout.screen_permission_guide, R.string.nav_permission_guide),
    }

    private companion object {
        val CODE_PATTERN = Regex("^[A-Z0-9]{6}$")
        const val REQUEST_NOTIFICATIONS = 1001
        const val KEY_OAUTH_STATE = "oauth_state"
    }
}
