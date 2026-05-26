package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val username: String, val password: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val username: String, val expiresIn: Long)

@Serializable
data class UserResponse(val id: Int, val username: String, val createdAt: String)

@Serializable
data class PrizeResponse(
    val id: Int,
    val year: Int,
    val category: String,
    val motivation: String?,
)

@Serializable
data class LaureateResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: String?,
    val deathDate: String?,
    val share: Int?,
    val affiliation: String?,
)

@Serializable
data class PrizeWithLaureatesResponse(
    val id: Int,
    val year: Int,
    val category: String,
    val motivation: String?,
    val laureates: List<LaureateResponse>,
)

@Serializable
data class FavoriteResponse(
    val prizeId: Int,
    val year: Int,
    val category: String,
    val motivation: String?,
    val addedAt: String,
)

@Serializable
data class ErrorResponse(val error: String, val status: Int)

@Serializable
data class MessageResponse(val message: String)
