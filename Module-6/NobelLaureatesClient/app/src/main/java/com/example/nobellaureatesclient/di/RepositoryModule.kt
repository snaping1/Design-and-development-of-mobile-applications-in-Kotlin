package com.example.nobellaureatesclient.di

import com.example.nobellaureatesclient.data.repository.AuthRepositoryImpl
import com.example.nobellaureatesclient.data.repository.FavoritesRepositoryImpl
import com.example.nobellaureatesclient.data.repository.PrizesRepositoryImpl
import com.example.nobellaureatesclient.domain.repository.AuthRepository
import com.example.nobellaureatesclient.domain.repository.FavoritesRepository
import com.example.nobellaureatesclient.domain.repository.PrizesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPrizesRepository(impl: PrizesRepositoryImpl): PrizesRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}
