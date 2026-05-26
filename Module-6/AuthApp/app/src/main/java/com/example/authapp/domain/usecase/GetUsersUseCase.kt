package com.example.authapp.domain.usecase

import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<List<User>> = runCatching {
        userRepository.getUsers()
    }
}
