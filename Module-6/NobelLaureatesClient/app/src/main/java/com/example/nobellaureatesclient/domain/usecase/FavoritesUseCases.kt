package com.example.nobellaureatesclient.domain.usecase

import com.example.nobellaureatesclient.domain.model.Favorite
import com.example.nobellaureatesclient.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(): Result<List<Favorite>> = repository.getFavorites()
}

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) {
    operator fun invoke(): Flow<Set<Int>> = repository.favoriteIds
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(prizeId: Int, isFavorite: Boolean): Result<Unit> =
        if (isFavorite) repository.remove(prizeId) else repository.add(prizeId)
}

class RefreshFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.refresh()
}
