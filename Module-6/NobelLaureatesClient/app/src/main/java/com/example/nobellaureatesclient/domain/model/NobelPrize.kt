package com.example.nobellaureatesclient.domain.model

data class NobelPrize(
    val id: Int,
    val year: Int,
    val category: NobelCategory,
    val motivation: String?,
    val laureates: List<Laureate> = emptyList(),
)
