package com.example.photocatalog.domain.repository

import com.example.photocatalog.domain.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    fun getPhotos(forceRefresh: Boolean = false): Flow<Result<List<PhotoEntity>>>

    suspend fun getPhotoById(id: String): PhotoEntity?
}
