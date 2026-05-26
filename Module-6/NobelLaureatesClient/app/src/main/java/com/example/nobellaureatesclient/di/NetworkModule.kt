package com.example.nobellaureatesclient.di

import android.util.Log
import com.example.nobellaureatesclient.data.local.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    @Named("public")
    fun providePublicHttpClient(json: Json): HttpClient = baseClient(json)

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthHttpClient(json: Json, tokenStorage: TokenStorage): HttpClient =
        baseClient(json).config {
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenStorage.currentToken()?.let { BearerTokens(it, "") }
                    }
                    refreshTokens {
                        tokenStorage.currentToken()?.let { BearerTokens(it, "") }
                    }
                    sendWithoutRequest { true }
                }
            }
        }

    private fun baseClient(json: Json): HttpClient = HttpClient(Android) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KtorClient", message)
                }
            }
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 20_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 20_000
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}
