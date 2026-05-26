package com.example.nobellaureatesclient.data.remote.api

import com.example.nobellaureatesclient.BuildConfig
import com.example.nobellaureatesclient.data.remote.dto.FavoriteDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FavoritesApi @Inject constructor(
    @Named("auth") private val client: HttpClient,
) {
    suspend fun list(): List<FavoriteDto> =
        client.get("${BuildConfig.SERVER_BASE_URL}/users/me/favorites").body()

    suspend fun add(prizeId: Int) {
        client.post("${BuildConfig.SERVER_BASE_URL}/users/me/favorites/$prizeId")
    }

    suspend fun remove(prizeId: Int) {
        client.delete("${BuildConfig.SERVER_BASE_URL}/users/me/favorites/$prizeId")
    }
}
