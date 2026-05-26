package com.example.authapp.data.remote.dto

import com.example.authapp.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String,
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
