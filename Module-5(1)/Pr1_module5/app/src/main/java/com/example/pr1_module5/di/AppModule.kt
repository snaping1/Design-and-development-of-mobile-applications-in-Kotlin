package com.example.pr1_module5.di

import android.content.Context
import com.example.pr1_module5.data.datasource.DiaryLocalDataSource
import com.example.pr1_module5.data.repository.DiaryRepositoryImpl
import com.example.pr1_module5.domain.repository.DiaryRepository
import com.example.pr1_module5.domain.usecase.DeleteEntryUseCase
import com.example.pr1_module5.domain.usecase.GetAllEntriesUseCase
import com.example.pr1_module5.domain.usecase.GetEntryUseCase
import com.example.pr1_module5.domain.usecase.SaveEntryUseCase

object AppModule {

    fun provideRepository(context: Context): DiaryRepository {
        val dataSource = DiaryLocalDataSource(context.applicationContext)
        return DiaryRepositoryImpl(dataSource)
    }

    fun provideGetAllEntriesUseCase(context: Context) =
        GetAllEntriesUseCase(provideRepository(context))

    fun provideSaveEntryUseCase(context: Context) =
        SaveEntryUseCase(provideRepository(context))

    fun provideDeleteEntryUseCase(context: Context) =
        DeleteEntryUseCase(provideRepository(context))

    fun provideGetEntryUseCase(context: Context) =
        GetEntryUseCase(provideRepository(context))
}