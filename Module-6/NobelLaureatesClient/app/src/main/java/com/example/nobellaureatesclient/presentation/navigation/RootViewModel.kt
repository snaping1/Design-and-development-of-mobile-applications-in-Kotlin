package com.example.nobellaureatesclient.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean?> = observeSession()
        .map { session -> session?.isValid() == true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
