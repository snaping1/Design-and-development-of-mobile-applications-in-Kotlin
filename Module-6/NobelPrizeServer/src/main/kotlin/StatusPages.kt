package com.example

import com.example.model.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

class ApiException(val status: HttpStatusCode, override val message: String) : RuntimeException(message)

private val statusPagesLog = LoggerFactory.getLogger("StatusPages")

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(cause.status, ErrorResponse(cause.message, cause.status.value))
        }
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(cause.message ?: "Bad request", HttpStatusCode.BadRequest.value),
            )
        }
        exception<NumberFormatException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid number format: ${cause.message}", HttpStatusCode.BadRequest.value),
            )
        }
        exception<Throwable> { call, cause ->
            statusPagesLog.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal server error", HttpStatusCode.InternalServerError.value),
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, ErrorResponse("Not found", status.value))
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(status, ErrorResponse("Unauthorized", status.value))
        }
    }
}
