package com.example.nobellaureatesclient.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.domain.usecase.GetPrizeDetailsUseCase
import com.example.nobellaureatesclient.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.nobellaureatesclient.domain.usecase.ToggleFavoriteUseCase
import com.example.nobellaureatesclient.presentation.common.UiState
import com.example.nobellaureatesclient.presentation.navigation.NobelDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrizeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDetails: GetPrizeDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
) : ViewModel() {

    private val year: Int = savedStateHandle.get<String>(NobelDestinations.ARG_YEAR)?.toIntOrNull() ?: 0
    private val category: NobelCategory =
        NobelCategory.fromApiCode(savedStateHandle.get<String>(NobelDestinations.ARG_CATEGORY))

    private val _state = MutableStateFlow<UiState<NobelPrize>>(UiState.Loading)
    val state: StateFlow<UiState<NobelPrize>> = _state.asStateFlow()

    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        load()
    }

    fun retry() = load()

    fun toggleFavorite() {
        val prize = (state.value as? UiState.Success)?.data ?: return
        val isFav = prize.id in favoriteIds.value
        viewModelScope.launch {
            toggleFavorite.invoke(prize.id, isFav)
        }
    }

    private fun load() {
        if (year == 0 || category == NobelCategory.ALL) {
            _state.value = UiState.Error("Некорректные параметры")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            getDetails(year, category)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure {
                    _state.value = UiState.Error(it.message ?: "Не удалось загрузить детали")
                }
        }
    }
}
