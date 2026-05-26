package com.example.authapp.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.domain.usecase.GetUserByIdUseCase
import com.example.authapp.domain.usecase.LogoutUseCase
import com.example.authapp.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[Routes.USER_ID_ARG])

    private val _state = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val state: StateFlow<UserDetailState> = _state.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    init {
        load()
    }

    fun load() {
        _state.value = UserDetailState.Loading
        viewModelScope.launch {
            getUserByIdUseCase(userId)
                .onSuccess { _state.value = UserDetailState.Success(it) }
                .onFailure {
                    _state.value = UserDetailState.Error(it.message ?: "Не удалось загрузить пользователя")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutEvent.tryEmit(Unit)
        }
    }
}
