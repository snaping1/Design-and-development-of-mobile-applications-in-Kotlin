package com.example.nobellaureatesclient.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.model.Favorite
import com.example.nobellaureatesclient.domain.usecase.GetFavoritesUseCase
import com.example.nobellaureatesclient.domain.usecase.ToggleFavoriteUseCase
import com.example.nobellaureatesclient.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Favorite>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Favorite>>> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun remove(prizeId: Int) {
        viewModelScope.launch {
            toggleFavorite.invoke(prizeId, isFavorite = true)
                .onSuccess {
                    val current = _state.value
                    if (current is UiState.Success) {
                        _state.update { UiState.Success(current.data.filterNot { it.prizeId == prizeId }) }
                    }
                }
        }
    }

    private fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            getFavorites()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure {
                    _state.value = UiState.Error(it.message ?: "Не удалось загрузить избранное")
                }
        }
    }
}
