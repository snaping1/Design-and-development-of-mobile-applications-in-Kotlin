package com.example.authapp.di

import com.example.authapp.data.local.TokenDataStore
import com.example.authapp.data.remote.KtorClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenDataStore: TokenDataStore): HttpClient =
        KtorClientFactory.create(tokenDataStore)
}
