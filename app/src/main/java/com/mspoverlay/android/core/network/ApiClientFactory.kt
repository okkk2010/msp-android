package com.mspoverlay.android.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClientFactory(
    private val baseUrl: String,
    private val tokenProvider: AuthTokenProvider,
    private val enableHttpLogging: Boolean = false,
) {
    fun createPublicOkHttpClient(): OkHttpClient {
        return baseClientBuilder().build()
    }

    fun createAuthenticatedOkHttpClient(): OkHttpClient {
        return baseClientBuilder()
            .addInterceptor(authInterceptor())
            .build()
    }

    fun createPublicRetrofit(): Retrofit {
        return retrofit(createPublicOkHttpClient())
    }

    fun createAuthenticatedRetrofit(): Retrofit {
        return retrofit(createAuthenticatedOkHttpClient())
    }

    private fun baseClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        if (enableHttpLogging) {
            builder.addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC),
            )
        }
        return builder
    }

    private fun authInterceptor(): Interceptor {
        return Interceptor { chain ->
            val token = tokenProvider.getAccessToken()
            val request = if (token.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request()
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            }
            chain.proceed(request)
        }
    }

    private fun retrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl())
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private fun normalizedBaseUrl(): String {
        return if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    }
}

