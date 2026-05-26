package com.example.nobellaureatesclient.domain.model

data class Favorite(
    val prizeId: Int,
    val year: Int,
    val category: NobelCategory,
    val motivation: String?,
    val addedAt: String,
)
