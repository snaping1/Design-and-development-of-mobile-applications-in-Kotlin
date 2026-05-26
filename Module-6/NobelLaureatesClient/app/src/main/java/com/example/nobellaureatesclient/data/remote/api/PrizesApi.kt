package com.example.nobellaureatesclient.data.remote.api

import com.example.nobellaureatesclient.BuildConfig
import com.example.nobellaureatesclient.data.remote.dto.PrizeDto
import com.example.nobellaureatesclient.data.remote.dto.PrizeWithLaureatesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PrizesApi @Inject constructor(
    @Named("auth") private val client: HttpClient,
) {
    suspend fun list(): List<PrizeDto> =
        client.get("${BuildConfig.SERVER_BASE_URL}/prizes").body()

    suspend fun details(year: Int, category: String): PrizeWithLaureatesDto =
        client.get("${BuildConfig.SERVER_BASE_URL}/prizes/$year/$category").body()
}
