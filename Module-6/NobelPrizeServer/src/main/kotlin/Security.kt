package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import com.example.model.ErrorResponse
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.Date

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expirationMinutes: Long,
) {
    init {
        require(secret.length >= 32) { "JWT secret must be at least 32 characters long" }
    }

    val expirationMillis: Long get() = expirationMinutes * 60_000L

    fun generateToken(userId: Int, username: String): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMillis))
            .sign(Algorithm.HMAC256(secret))
}

fun loadJwtConfig(environment: ApplicationEnvironment): JwtConfig {
    val cfg = environment.config.config("jwt")
    return JwtConfig(
        secret = cfg.property("secret").getString(),
        issuer = cfg.property("issuer").getString(),
        audience = cfg.property("audience").getString(),
        realm = cfg.property("realm").getString(),
        expirationMinutes = cfg.property("expirationMinutes").getString().toLong(),
    )
}

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    authentication {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val username = credential.payload.getClaim("username").asString()
                if (userId != null && !username.isNullOrBlank() &&
                    credential.payload.audience.contains(jwtConfig.audience)
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Token is not valid or has expired", HttpStatusCode.Unauthorized.value),
                )
            }
        }
    }
}

val JWTPrincipal.userId: Int
    get() = payload.getClaim("userId").asInt()

val JWTPrincipal.username: String
    get() = payload.getClaim("username").asString()
