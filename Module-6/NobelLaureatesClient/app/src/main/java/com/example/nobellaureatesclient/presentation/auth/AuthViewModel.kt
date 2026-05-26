package com.example.nobellaureatesclient.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.usecase.LoginUseCase
import com.example.nobellaureatesclient.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthMode { LOGIN, REGISTER }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _events = Channel<Unit>(Channel.BUFFERED)
    val authSuccess = _events.receiveAsFlow()

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    fun setMode(mode: AuthMode) {
        _state.update { it.copy(mode = mode, error = null) }
    }

    fun submit() {
        val s = _state.value
        val username = s.username.trim()
        val password = s.password

        val clientError = when {
            username.length < 3 -> "Имя пользователя должно быть не короче 3 символов"
            password.length < 8 -> "Пароль должен быть не короче 8 символов"
            else -> null
        }
        if (clientError != null) {
            _state.update { it.copy(error = clientError) }
            return
        }

        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val result = when (s.mode) {
                AuthMode.LOGIN -> login(username, password)
                AuthMode.REGISTER -> register(username, password)
            }
            result
                .onSuccess {
                    _state.update { it.copy(loading = false, password = "") }
                    _events.send(Unit)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = throwable.message ?: "Не удалось выполнить вход",
                        )
                    }
                }
        }
    }
}
