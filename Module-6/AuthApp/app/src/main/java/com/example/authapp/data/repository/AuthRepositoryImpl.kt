package com.example.authapp.data.repository

import com.example.authapp.data.local.TokenDataStore
import com.example.authapp.data.remote.ApiService
import com.example.authapp.data.remote.dto.LoginRequestDto
import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.AuthRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): User {
        val response = try {
            api.login(LoginRequestDto(username = username, password = password))
        } catch (e: ClientRequestException) {
            throw if (e.response.status == HttpStatusCode.BadRequest ||
                e.response.status == HttpStatusCode.Unauthorized
            ) {
                IllegalStateException("Неверный логин или пароль")
            } else e
        } catch (e: ServerResponseException) {
            throw IllegalStateException("Сервер недоступен. Попробуйте позже")
        }
        tokenDataStore.saveTokens(response.accessToken, response.refreshToken)
        return response.toDomain()
    }

    override suspend fun logout() {
        tokenDataStore.clear()
    }

    override fun isLoggedIn(): Flow<Boolean> = tokenDataStore.isLoggedIn
}
