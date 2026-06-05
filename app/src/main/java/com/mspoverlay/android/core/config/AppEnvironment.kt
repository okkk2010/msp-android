package com.mspoverlay.android.core.config

import com.mspoverlay.android.BuildConfig

object AppEnvironment {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.trimEnd('/')
    val httpLoggingEnabled: Boolean = BuildConfig.HTTP_LOGGING_ENABLED
}
