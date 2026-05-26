package com.example.pr2_module5.domain.usecase

import com.example.pr2_module5.domain.model.PhotoEntry
import com.example.pr2_module5.domain.repository.PhotoRepository
import java.io.File

class SavePhotoUseCase(private val repository: PhotoRepository) {
    fun createFile(): File = repository.createPhotoFile()
    fun addPhoto(file: File): PhotoEntry = repository.addPhoto(file)
}