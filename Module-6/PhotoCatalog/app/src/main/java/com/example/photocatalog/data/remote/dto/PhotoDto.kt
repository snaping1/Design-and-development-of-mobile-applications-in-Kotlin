package com.example.photocatalog.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDto(
    @Json(name = "id") val id: String,
    @Json(name = "author") val author: String,
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int,
    @Json(name = "url") val url: String,
    @Json(name = "download_url") val downloadUrl: String
)
