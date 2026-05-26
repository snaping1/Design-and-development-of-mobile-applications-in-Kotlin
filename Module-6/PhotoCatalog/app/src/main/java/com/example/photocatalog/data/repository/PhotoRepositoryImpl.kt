package com.example.photocatalog.data.repository

import com.example.photocatalog.data.mapper.toEntityList
import com.example.photocatalog.data.remote.PicsumApi
import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.domain.repository.PhotoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val api: PicsumApi
) : PhotoRepository {

    private val cacheMutex = Mutex()
    private var cachedPhotos: List<PhotoEntity> = emptyList()
    private var cacheTimestamp: Long = 0L

    override fun getPhotos(forceRefresh: Boolean): Flow<Result<List<PhotoEntity>>> = flow {
        val cached = cacheMutex.withLock { cachedPhotos }
        if (!forceRefresh && cached.isNotEmpty() && !isCacheStale()) {
            emit(Result.success(cached))
            return@flow
        }

        val result = runCatching { fetchWithRetry() }
        result
            .onSuccess { photos ->
                cacheMutex.withLock {
                    cachedPhotos = photos
                    cacheTimestamp = System.currentTimeMillis()
                }
                emit(Result.success(photos))
            }
            .onFailure { error ->
                if (cached.isNotEmpty()) {
                    emit(Result.success(cached))
                } else {
                    emit(Result.failure(error))
                }
            }
    }

    override suspend fun getPhotoById(id: String): PhotoEntity? =
        cacheMutex.withLock { cachedPhotos.firstOrNull { it.id == id } }

    private suspend fun fetchWithRetry(
        attempts: Int = MAX_RETRY_ATTEMPTS,
        initialDelayMs: Long = INITIAL_RETRY_DELAY_MS
    ): List<PhotoEntity> {
        var currentDelay = initialDelayMs
        repeat(attempts - 1) {
            try {
                return api.getPhotos().toEntityList()
            } catch (e: IOException) {
                delay(currentDelay)
                currentDelay = (currentDelay * RETRY_BACKOFF_FACTOR).toLong()
            }
        }
        return api.getPhotos().toEntityList()
    }

    private fun isCacheStale(): Boolean =
        System.currentTimeMillis() - cacheTimestamp > CACHE_TTL_MS

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 500L
        private const val RETRY_BACKOFF_FACTOR = 2.0
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }
}
