package com.example.authapp.data.remote

import com.example.authapp.data.local.TokenDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object KtorClientFactory {

    const val HOST = "dummyjson.com"

    fun create(tokenDataStore: TokenDataStore): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                }
            )
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("KtorClient", message)
                }
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = HOST
            }
            contentType(ContentType.Application.Json)
            // AuthInterceptor: на каждый запрос подкладываем Bearer-токен из DataStore
            val token = runBlocking { tokenDataStore.getAccessToken() }
            if (!token.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
