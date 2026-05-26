package com.example

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

const val SECURITY_SCHEME_JWT = "bearerAuth"

fun Application.configureOpenApi() {
    install(OpenApi) {
        info {
            title = "Nobel Prize API"
            version = "1.0.0"
            description = """
                REST API serving Nobel Prize data with JWT authentication and
                per-user favorites. Use `POST /auth/register` or `POST /auth/login`
                to obtain a JWT, then click "Authorize" and paste the token
                (without the `Bearer ` prefix).
            """.trimIndent()
        }
        server {
            url = "http://localhost:8080"
            description = "Local development server"
        }
        security {
            securityScheme(SECURITY_SCHEME_JWT) {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "JWT"
                description = "Paste the token returned by /auth/login or /auth/register."
            }
        }
    }
}

fun Routing.configureDocsRoutes() {
    route("openapi.json") {
        openApi()
    }
    route("swagger") {
        swaggerUI("/openapi.json")
    }
    route("redoc") {
        redoc("/openapi.json")
    }
}
