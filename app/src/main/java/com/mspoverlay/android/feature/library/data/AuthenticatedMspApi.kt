package com.mspoverlay.android.feature.library.data

import com.mspoverlay.android.core.network.dto.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthenticatedMspApi {
    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<UserMeDto>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): ApiResponse<AuthTokenDto>

    @POST("api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @GET("api/library")
    suspend fun getLibrary(): ApiResponse<List<LibraryItemDto>>

    @POST("api/library")
    suspend fun saveToLibrary(@Body request: LibrarySaveRequestDto): ApiResponse<Unit>

    @DELETE("api/library/{overlayId}")
    suspend fun deleteFromLibrary(@Path("overlayId") overlayDatabaseId: Long): ApiResponse<Unit>
}

