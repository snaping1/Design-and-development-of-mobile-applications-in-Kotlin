package com.example.nobellaureatesclient.domain.usecase

import com.example.nobellaureatesclient.domain.model.AuthSession
import com.example.nobellaureatesclient.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String): Result<AuthSession> =
        repository.login(username.trim(), password)
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String): Result<AuthSession> =
        repository.register(username.trim(), password)
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.logout()
}

class ObserveSessionUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = repository.session
}
