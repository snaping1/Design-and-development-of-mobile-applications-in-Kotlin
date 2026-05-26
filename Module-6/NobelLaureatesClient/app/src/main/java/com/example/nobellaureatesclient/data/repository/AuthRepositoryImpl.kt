package com.example.nobellaureatesclient.data.repository

import com.example.nobellaureatesclient.data.local.TokenStorage
import com.example.nobellaureatesclient.data.remote.api.AuthApi
import com.example.nobellaureatesclient.data.remote.runCatchingApi
import com.example.nobellaureatesclient.domain.model.AuthSession
import com.example.nobellaureatesclient.domain.repository.AuthRepository
import com.example.nobellaureatesclient.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    private val favoritesRepository: FavoritesRepository,
) : AuthRepository {

    override val session: Flow<AuthSession?> = tokenStorage.session

    override suspend fun currentSession(): AuthSession? = tokenStorage.currentSession()

    override suspend fun login(username: String, password: String): Result<AuthSession> =
        runCatchingApi { api.login(username, password) }.mapCatching { dto ->
            tokenStorage.save(dto.token, dto.username, dto.expiresIn)
            favoritesRepository.clearCache()
            AuthSession(dto.token, dto.username, System.currentTimeMillis() + dto.expiresIn)
        }

    override suspend fun register(username: String, password: String): Result<AuthSession> =
        runCatchingApi { api.register(username, password) }.mapCatching { dto ->
            tokenStorage.save(dto.token, dto.username, dto.expiresIn)
            favoritesRepository.clearCache()
            AuthSession(dto.token, dto.username, System.currentTimeMillis() + dto.expiresIn)
        }

    override suspend fun logout() {
        tokenStorage.clear()
        favoritesRepository.clearCache()
    }
}
