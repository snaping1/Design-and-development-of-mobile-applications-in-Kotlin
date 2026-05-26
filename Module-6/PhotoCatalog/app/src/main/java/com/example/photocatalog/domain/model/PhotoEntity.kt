package com.example.photocatalog.domain.model

data class PhotoEntity(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val sourceUrl: String
)
