package com.example.authapp.data.repository

import com.example.authapp.data.remote.ApiService
import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : UserRepository {

    override suspend fun getUsers(): List<User> =
        api.getUsers().users.map { it.toDomain() }

    override suspend fun getUserById(id: Int): User =
        api.getUserById(id).toDomain()
}
