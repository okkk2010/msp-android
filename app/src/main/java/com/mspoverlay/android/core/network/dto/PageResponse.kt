package com.mspoverlay.android.core.network.dto

data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
)

