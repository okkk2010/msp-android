package com.mspoverlay.android.core.network.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
)

