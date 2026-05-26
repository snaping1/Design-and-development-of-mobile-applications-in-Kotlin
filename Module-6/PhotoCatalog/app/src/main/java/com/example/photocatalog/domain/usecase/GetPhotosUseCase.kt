package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val repository: PhotoRepository
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<Result<List<PhotoEntity>>> =
        repository.getPhotos(forceRefresh)
}
