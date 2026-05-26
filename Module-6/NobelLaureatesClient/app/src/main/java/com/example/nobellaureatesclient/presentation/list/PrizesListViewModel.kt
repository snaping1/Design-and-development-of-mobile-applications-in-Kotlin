package com.example.nobellaureatesclient.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.domain.usecase.GetPrizesUseCase
import com.example.nobellaureatesclient.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.nobellaureatesclient.domain.usecase.RefreshFavoritesUseCase
import com.example.nobellaureatesclient.domain.usecase.ToggleFavoriteUseCase
import com.example.nobellaureatesclient.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrizesListUiModel(
    val yearInput: String = "",
    val selectedCategory: NobelCategory = NobelCategory.ALL,
    val prizes: UiState<List<NobelPrize>> = UiState.Loading,
)

@HiltViewModel
class PrizesListViewModel @Inject constructor(
    private val getPrizes: GetPrizesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val refreshFavorites: RefreshFavoritesUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PrizesListUiModel())
    val state: StateFlow<PrizesListUiModel> = _state.asStateFlow()

    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        load()
        viewModelScope.launch { refreshFavorites() }
    }

    fun onYearChange(value: String) {
        if (value.length <= 4 && value.all { it.isDigit() }) {
            _state.update { it.copy(yearInput = value) }
        }
    }

    fun onCategoryChange(category: NobelCategory) {
        _state.update { it.copy(selectedCategory = category) }
        load()
    }

    fun applyFilters() = load()

    fun retry() = load()

    fun toggleFavorite(prize: NobelPrize) {
        val isFav = prize.id in favoriteIds.value
        viewModelScope.launch {
            toggleFavorite.invoke(prize.id, isFav)
        }
    }

    private fun load() {
        val current = _state.value
        val year = current.yearInput.toIntOrNull()
        _state.update { it.copy(prizes = UiState.Loading) }
        viewModelScope.launch {
            getPrizes(year, current.selectedCategory)
                .onSuccess { prizes ->
                    _state.update { it.copy(prizes = UiState.Success(prizes)) }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(prizes = UiState.Error(throwable.message ?: "Неизвестная ошибка"))
                    }
                }
        }
    }
}
