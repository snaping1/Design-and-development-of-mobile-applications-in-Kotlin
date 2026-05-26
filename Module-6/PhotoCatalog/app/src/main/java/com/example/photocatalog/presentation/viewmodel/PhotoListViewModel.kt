package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.domain.usecase.GetPhotosUseCase
import com.example.photocatalog.presentation.common.UiState
import com.example.photocatalog.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<PhotoEntity>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PhotoEntity>>> = _state.asStateFlow()

    init {
        load(forceRefresh = false)
    }

    fun retry() = load(forceRefresh = true)

    private fun load(forceRefresh: Boolean) {
        viewModelScope.launch {
            getPhotosUseCase(forceRefresh)
                .onStart { _state.value = UiState.Loading }
                .catch { e -> _state.value = UiState.Error(e.toUserMessage()) }
                .collect { result ->
                    result
                        .onSuccess { photos -> _state.value = UiState.Success(photos) }
                        .onFailure { e -> _state.value = UiState.Error(e.toUserMessage()) }
                }
        }
    }
}
