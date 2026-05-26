package com.example.authapp.presentation.userdetail

import com.example.authapp.domain.model.User

sealed interface UserDetailState {
    data object Loading : UserDetailState
    data class Error(val message: String) : UserDetailState
    data class Success(val user: User) : UserDetailState
}
