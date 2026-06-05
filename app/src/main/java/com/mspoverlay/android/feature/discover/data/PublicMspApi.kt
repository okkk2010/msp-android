package com.mspoverlay.android.feature.discover.data

import com.mspoverlay.android.core.network.dto.ApiResponse
import com.mspoverlay.android.core.network.dto.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicMspApi {
    @GET("api/platforms")
    suspend fun getPlatforms(): ApiResponse<List<PlatformDto>>

    @GET("api/games")
    suspend fun getGames(@Query("platform") platform: String): ApiResponse<List<GameDto>>

    @GET("api/overlays")
    suspend fun getOverlays(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("platform") platform: String? = null,
        @Query("game") game: String? = null,
        @Query("code") code: String? = null,
        @Query("sort") sort: String? = null,
    ): ApiResponse<PageResponse<OverlaySummaryDto>>

    @GET("api/overlays/{overlayId}")
    suspend fun getOverlayDetail(@Path("overlayId") overlayId: String): ApiResponse<OverlayDetailDto>

    @GET("api/overlays/code/{code}")
    suspend fun getOverlayByCode(@Path("code") code: String): ApiResponse<OverlayCodeLoadDto>
}
