package com.example.nobellaureatesclient.domain.repository

import com.example.nobellaureatesclient.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteIds: Flow<Set<Int>>
    suspend fun getFavorites(): Result<List<Favorite>>
    suspend fun add(prizeId: Int): Result<Unit>
    suspend fun remove(prizeId: Int): Result<Unit>
    suspend fun refresh(): Result<Unit>
    fun clearCache()
}
