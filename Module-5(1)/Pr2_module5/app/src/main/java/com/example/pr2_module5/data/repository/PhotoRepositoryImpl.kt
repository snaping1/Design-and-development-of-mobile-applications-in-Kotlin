package com.example.pr2_module5.data.repository

import com.example.pr2_module5.data.datasource.PhotoLocalDataSource
import com.example.pr2_module5.domain.model.PhotoEntry
import com.example.pr2_module5.domain.repository.PhotoRepository
import java.io.File

class PhotoRepositoryImpl(
    private val dataSource: PhotoLocalDataSource
) : PhotoRepository {

    override fun getAllPhotos(): List<PhotoEntry> = dataSource.readAllPhotos()

    override fun createPhotoFile(): File = dataSource.createPhotoFile()

    override fun addPhoto(file: File): PhotoEntry = dataSource.addPhoto(file)

    override fun exportPhotoToGallery(photoEntry: PhotoEntry): Boolean =
        dataSource.exportToGallery(photoEntry)
}