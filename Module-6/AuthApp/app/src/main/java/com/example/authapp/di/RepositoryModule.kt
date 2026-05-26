package com.example.authapp.di

import com.example.authapp.data.repository.AuthRepositoryImpl
import com.example.authapp.data.repository.UserRepositoryImpl
import com.example.authapp.domain.repository.AuthRepository
import com.example.authapp.domain.repository.UserRepository
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
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
