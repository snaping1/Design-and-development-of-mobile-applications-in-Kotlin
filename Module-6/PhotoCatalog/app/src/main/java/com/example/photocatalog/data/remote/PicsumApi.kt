package com.example.photocatalog.data.remote

import com.example.photocatalog.data.remote.dto.PhotoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {

    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): List<PhotoDto>

    companion object {
        const val BASE_URL = "https://picsum.photos/"
    }
}
