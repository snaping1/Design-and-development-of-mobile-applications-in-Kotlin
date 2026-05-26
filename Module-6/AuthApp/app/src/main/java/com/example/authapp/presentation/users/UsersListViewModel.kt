package com.example.authapp.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UsersListState>(UsersListState.Loading)
    val state: StateFlow<UsersListState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _state.value = UsersListState.Loading
        viewModelScope.launch {
            getUsersUseCase()
                .onSuccess { _state.value = UsersListState.Success(it) }
                .onFailure {
                    _state.value = UsersListState.Error(it.message ?: "Не удалось загрузить пользователей")
                }
        }
    }
}
