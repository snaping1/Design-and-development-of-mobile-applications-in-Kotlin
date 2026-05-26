package com.example.pr2_module5.domain.repository

import com.example.pr2_module5.domain.model.PhotoEntry
import java.io.File

interface PhotoRepository {
    fun getAllPhotos(): List<PhotoEntry>
    fun createPhotoFile(): File
    fun addPhoto(file: File): PhotoEntry
    fun exportPhotoToGallery(photoEntry: PhotoEntry): Boolean
}