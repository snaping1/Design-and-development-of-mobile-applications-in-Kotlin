package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

fun Application.module() {
    val jwtConfig = loadJwtConfig(environment)

    DatabaseFactory.init(environment)

    install(CallLogging) {
        level = Level.INFO
    }

    configureHttp()
    configureSerialization()
    configureOpenApi()
    configureSecurity(jwtConfig)
    configureStatusPages()
    configureRouting(jwtConfig)
}
