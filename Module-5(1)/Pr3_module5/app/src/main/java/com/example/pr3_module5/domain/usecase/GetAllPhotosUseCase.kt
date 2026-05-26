package com.example.pr3_module5.domain.usecase

import com.example.pr3_module5.domain.model.PhotoEntry
import com.example.pr3_module5.domain.repository.PhotoRepository

class GetAllPhotosUseCase(private val repository: PhotoRepository) {
    operator fun invoke(): List<PhotoEntry> = repository.getAllPhotos()
}