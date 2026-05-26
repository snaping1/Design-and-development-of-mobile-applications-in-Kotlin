package com.example.nobellaureatesclient.domain.model

data class Laureate(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: String?,
    val deathDate: String?,
    val share: Int?,
    val affiliation: String?,
) {
    val fullName: String get() = "$firstName $lastName".trim()
}
