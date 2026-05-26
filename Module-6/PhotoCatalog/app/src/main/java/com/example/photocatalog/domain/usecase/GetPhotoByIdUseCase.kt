package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.model.PhotoEntity
import com.example.photocatalog.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoByIdUseCase @Inject constructor(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(id: String): PhotoEntity? = repository.getPhotoById(id)
}
