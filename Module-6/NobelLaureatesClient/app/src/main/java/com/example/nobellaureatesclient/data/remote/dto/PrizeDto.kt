package com.example.nobellaureatesclient.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrizeDto(
    val id: Int,
    val year: Int,
    val category: String,
    val motivation: String? = null,
)

@Serializable
data class LaureateDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: String? = null,
    val deathDate: String? = null,
    val share: Int? = null,
    val affiliation: String? = null,
)

@Serializable
data class PrizeWithLaureatesDto(
    val id: Int,
    val year: Int,
    val category: String,
    val motivation: String? = null,
    val laureates: List<LaureateDto> = emptyList(),
)

@Serializable
data class FavoriteDto(
    val prizeId: Int,
    val year: Int,
    val category: String,
    val motivation: String? = null,
    val addedAt: String,
)
