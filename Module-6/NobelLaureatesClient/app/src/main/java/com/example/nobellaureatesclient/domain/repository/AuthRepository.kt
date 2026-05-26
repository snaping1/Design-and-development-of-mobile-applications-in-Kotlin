package com.example.nobellaureatesclient.domain.repository

import com.example.nobellaureatesclient.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<AuthSession?>
    suspend fun currentSession(): AuthSession?
    suspend fun login(username: String, password: String): Result<AuthSession>
    suspend fun register(username: String, password: String): Result<AuthSession>
    suspend fun logout()
}
