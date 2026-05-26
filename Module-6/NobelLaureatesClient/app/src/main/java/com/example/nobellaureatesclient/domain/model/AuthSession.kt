package com.example.nobellaureatesclient.domain.model

data class AuthSession(
    val token: String,
    val username: String,
    val expiresAtMillis: Long,
) {
    fun isValid(nowMillis: Long = System.currentTimeMillis()): Boolean =
        token.isNotBlank() && expiresAtMillis > nowMillis
}
