package com.example.authapp.domain.repository

import com.example.authapp.domain.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User
}
