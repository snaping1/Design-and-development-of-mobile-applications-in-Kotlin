package com.example.authapp.presentation.users

import com.example.authapp.domain.model.User

sealed interface UsersListState {
    data object Loading : UsersListState
    data class Error(val message: String) : UsersListState
    data class Success(val users: List<User>) : UsersListState
}
