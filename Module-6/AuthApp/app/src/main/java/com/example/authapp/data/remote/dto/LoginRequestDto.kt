package com.example.authapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int = 60,
)
