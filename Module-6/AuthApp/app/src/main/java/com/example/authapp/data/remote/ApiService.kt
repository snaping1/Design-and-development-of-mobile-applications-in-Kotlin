package com.example.authapp.data.remote

import com.example.authapp.data.remote.dto.LoginRequestDto
import com.example.authapp.data.remote.dto.LoginResponseDto
import com.example.authapp.data.remote.dto.UserDto
import com.example.authapp.data.remote.dto.UsersResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun login(request: LoginRequestDto): LoginResponseDto =
        client.post("/auth/login") { setBody(request) }.body()

    suspend fun getUsers(): UsersResponseDto =
        client.get("/users").body()

    suspend fun getUserById(id: Int): UserDto =
        client.get("/users/$id").body()
}
