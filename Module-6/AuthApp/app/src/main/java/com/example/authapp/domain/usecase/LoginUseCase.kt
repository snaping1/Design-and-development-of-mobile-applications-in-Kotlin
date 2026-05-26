package com.example.authapp.domain.usecase

import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String): Result<User> = runCatching {
        require(username.isNotBlank()) { "Username не может быть пустым" }
        require(password.isNotBlank()) { "Password не может быть пустым" }
        authRepository.login(username.trim(), password)
    }
}
