package com.example.authapp.data.remote.dto

import com.example.authapp.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String? = null,
    val image: String,
    val accessToken: String,
    val refreshToken: String? = null,
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        image = image,
    )
}
