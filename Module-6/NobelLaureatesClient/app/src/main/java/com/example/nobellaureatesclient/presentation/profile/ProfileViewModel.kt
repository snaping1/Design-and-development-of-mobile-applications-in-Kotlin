package com.example.nobellaureatesclient.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.model.AuthSession
import com.example.nobellaureatesclient.domain.usecase.LogoutUseCase
import com.example.nobellaureatesclient.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val session: StateFlow<AuthSession?> = observeSession()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }
}
