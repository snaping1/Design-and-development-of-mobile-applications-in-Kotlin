package com.example.authapp.domain.repository

import com.example.authapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): User
    suspend fun logout()
    fun isLoggedIn(): Flow<Boolean>
}
