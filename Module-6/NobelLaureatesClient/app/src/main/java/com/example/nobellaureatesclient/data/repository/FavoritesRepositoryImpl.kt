package com.example.nobellaureatesclient.data.repository

import com.example.nobellaureatesclient.data.mapper.toDomain
import com.example.nobellaureatesclient.data.remote.api.FavoritesApi
import com.example.nobellaureatesclient.data.remote.runCatchingApi
import com.example.nobellaureatesclient.domain.model.Favorite
import com.example.nobellaureatesclient.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val api: FavoritesApi,
) : FavoritesRepository {

    private val mutex = Mutex()
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    override suspend fun getFavorites(): Result<List<Favorite>> = runCatchingApi {
        val favorites = api.list().map { it.toDomain() }
        _favoriteIds.value = favorites.mapTo(mutableSetOf()) { it.prizeId }
        favorites.sortedByDescending { it.addedAt }
    }

    override suspend fun add(prizeId: Int): Result<Unit> = runCatchingApi {
        api.add(prizeId)
        mutex.withLock {
            _favoriteIds.update { it + prizeId }
        }
    }

    override suspend fun remove(prizeId: Int): Result<Unit> = runCatchingApi {
        api.remove(prizeId)
        mutex.withLock {
            _favoriteIds.update { it - prizeId }
        }
    }

    override suspend fun refresh(): Result<Unit> = getFavorites().map { }

    override fun clearCache() {
        _favoriteIds.value = emptySet()
    }
}
