package com.example.nobellaureatesclient.data.remote.api

import com.example.nobellaureatesclient.BuildConfig
import com.example.nobellaureatesclient.data.remote.dto.AuthResponseDto
import com.example.nobellaureatesclient.data.remote.dto.LoginRequestDto
import com.example.nobellaureatesclient.data.remote.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthApi @Inject constructor(
    @Named("public") private val client: HttpClient,
) {
    suspend fun register(username: String, password: String): AuthResponseDto =
        client.post("${BuildConfig.SERVER_BASE_URL}/auth/register") {
            setBody(RegisterRequestDto(username, password))
        }.body()

    suspend fun login(username: String, password: String): AuthResponseDto =
        client.post("${BuildConfig.SERVER_BASE_URL}/auth/login") {
            setBody(LoginRequestDto(username, password))
        }.body()
}
