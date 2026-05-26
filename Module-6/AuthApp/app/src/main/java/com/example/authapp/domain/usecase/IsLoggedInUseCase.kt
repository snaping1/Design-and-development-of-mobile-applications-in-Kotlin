package com.example.authapp.domain.usecase

import com.example.authapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Boolean> = authRepository.isLoggedIn()
}
