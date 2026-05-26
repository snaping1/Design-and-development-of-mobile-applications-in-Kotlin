package com.example.pr2_module5.di

import android.content.Context
import com.example.pr2_module5.data.datasource.PhotoLocalDataSource
import com.example.pr2_module5.data.repository.PhotoRepositoryImpl
import com.example.pr2_module5.domain.repository.PhotoRepository
import com.example.pr2_module5.domain.usecase.ExportPhotoUseCase
import com.example.pr2_module5.domain.usecase.GetAllPhotosUseCase
import com.example.pr2_module5.domain.usecase.SavePhotoUseCase

object GalleryModule {

    fun provideRepository(context: Context): PhotoRepository {
        val dataSource = PhotoLocalDataSource(context.applicationContext)
        return PhotoRepositoryImpl(dataSource)
    }

    fun provideGetAllPhotosUseCase(context: Context) =
        GetAllPhotosUseCase(provideRepository(context))

    fun provideSavePhotoUseCase(context: Context) =
        SavePhotoUseCase(provideRepository(context))

    fun provideExportPhotoUseCase(context: Context) =
        ExportPhotoUseCase(provideRepository(context))
}