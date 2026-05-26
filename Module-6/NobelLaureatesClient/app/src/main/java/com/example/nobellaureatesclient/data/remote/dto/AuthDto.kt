package com.example.nobellaureatesclient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(val username: String, val password: String)

@Serializable
data class LoginRequestDto(val username: String, val password: String)

@Serializable
data class AuthResponseDto(
    val token: String,
    val username: String,
    val expiresIn: Long
)

@Serializable
data class ErrorResponseDto(val error: String, val status: Int)

@Serializable
data class MessageResponseDto(val message: String)
