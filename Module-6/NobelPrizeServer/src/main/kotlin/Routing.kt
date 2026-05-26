package com.example

import com.example.model.AuthResponse
import com.example.model.ErrorResponse
import com.example.model.FavoriteResponse
import com.example.model.LaureateResponse
import com.example.model.LoginRequest
import com.example.model.MessageResponse
import com.example.model.PrizeResponse
import com.example.model.PrizeWithLaureatesResponse
import com.example.model.RegisterRequest
import com.example.repository.AddResult
import com.example.repository.FavoriteRepository
import com.example.repository.PrizeRepository
import com.example.repository.UserRepository
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(jwtConfig: JwtConfig) {
    val userRepo = UserRepository()
    val prizeRepo = PrizeRepository()
    val favoriteRepo = FavoriteRepository()

    routing {
        configureDocsRoutes()

        get("/", {
            tags = listOf("Meta")
            summary = "Welcome message"
            description = "Liveness banner for the API root."
            response {
                HttpStatusCode.OK to { body<MessageResponse>() }
            }
        }) {
            call.respond(MessageResponse("Nobel Prize API is running"))
        }
        get("/health", {
            tags = listOf("Meta")
            summary = "Health check"
            response { HttpStatusCode.OK to { body<MessageResponse>() } }
        }) {
            call.respond(MessageResponse("ok"))
        }

        route("/auth") {
            post("/register", {
                tags = listOf("Auth")
                summary = "Register a new user"
                description = "Creates a new account and returns a JWT valid for 30 minutes."
                request { body<RegisterRequest>() }
                response {
                    HttpStatusCode.Created to {
                        description = "Account created; JWT issued."
                        body<AuthResponse>()
                    }
                    HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                    HttpStatusCode.Conflict to {
                        description = "Username already taken."
                        body<ErrorResponse>()
                    }
                }
            }) {
                val request = call.receive<RegisterRequest>()
                validateCredentials(request.username, request.password)

                val existing = userRepo.findByUsername(request.username)
                if (existing != null) {
                    throw ApiException(HttpStatusCode.Conflict, "Username already taken")
                }
                val user = userRepo.create(request.username, request.password)
                val token = jwtConfig.generateToken(user.id, user.username)
                call.respond(
                    HttpStatusCode.Created,
                    AuthResponse(token = token, username = user.username, expiresIn = jwtConfig.expirationMillis),
                )
            }

            post("/login", {
                tags = listOf("Auth")
                summary = "Log in"
                description = "Verifies credentials and returns a JWT valid for 30 minutes."
                request { body<LoginRequest>() }
                response {
                    HttpStatusCode.OK to { body<AuthResponse>() }
                    HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                    HttpStatusCode.Unauthorized to {
                        description = "Invalid username or password."
                        body<ErrorResponse>()
                    }
                }
            }) {
                val request = call.receive<LoginRequest>()
                validateCredentials(request.username, request.password)

                val user = userRepo.findByUsername(request.username)
                    ?: throw ApiException(HttpStatusCode.Unauthorized, "Invalid username or password")
                if (!userRepo.verifyPassword(request.password, user.passwordHash)) {
                    throw ApiException(HttpStatusCode.Unauthorized, "Invalid username or password")
                }
                val token = jwtConfig.generateToken(user.id, user.username)
                call.respond(AuthResponse(token = token, username = user.username, expiresIn = jwtConfig.expirationMillis))
            }
        }

        authenticate("auth-jwt") {
            route("/prizes") {
                get({
                    tags = listOf("Prizes")
                    summary = "List all prizes"
                    description = "Returns every Nobel Prize, newest year first."
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    response {
                        HttpStatusCode.OK to { body<List<PrizeResponse>>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    call.respond(prizeRepo.listAll())
                }
                get("/{year}/{category}", {
                    tags = listOf("Prizes")
                    summary = "Get a specific prize"
                    description = "Returns the prize for the given year and category, including its laureates."
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    request {
                        pathParameter<Int>("year") {
                            description = "Year of the prize (e.g. 1921)"
                            example("Einstein") { value = 1921 }
                        }
                        pathParameter<String>("category") {
                            description = "Category — one of physics, chemistry, medicine, literature, peace, economics"
                            example("Physics") { value = "physics" }
                        }
                    }
                    response {
                        HttpStatusCode.OK to { body<PrizeWithLaureatesResponse>() }
                        HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                        HttpStatusCode.NotFound to { body<ErrorResponse>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    val (year, category) = parseYearCategory(call.parameters)
                    val prize = prizeRepo.findByYearAndCategory(year, category)
                        ?: throw ApiException(
                            HttpStatusCode.NotFound,
                            "Prize for $year/$category not found",
                        )
                    call.respond(prize)
                }
                get("/{year}/{category}/laureates", {
                    tags = listOf("Prizes")
                    summary = "Get laureates for a prize"
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    request {
                        pathParameter<Int>("year") { description = "Year of the prize" }
                        pathParameter<String>("category") { description = "Prize category" }
                    }
                    response {
                        HttpStatusCode.OK to { body<List<LaureateResponse>>() }
                        HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                        HttpStatusCode.NotFound to { body<ErrorResponse>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    val (year, category) = parseYearCategory(call.parameters)
                    val laureates = prizeRepo.findLaureatesForPrize(year, category)
                        ?: throw ApiException(
                            HttpStatusCode.NotFound,
                            "Prize for $year/$category not found",
                        )
                    call.respond(laureates)
                }
            }

            route("/users/me/favorites") {
                get({
                    tags = listOf("Favorites")
                    summary = "List my favorite prizes"
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    response {
                        HttpStatusCode.OK to { body<List<FavoriteResponse>>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    val principal = call.principal<JWTPrincipal>()!!
                    call.respond(favoriteRepo.listForUser(principal.userId))
                }
                post("/{prizeId}", {
                    tags = listOf("Favorites")
                    summary = "Add a prize to favorites"
                    description = "Idempotent: re-adding the same prize returns 200 with an informational message."
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    request {
                        pathParameter<Int>("prizeId") { description = "Numeric prize id" }
                    }
                    response {
                        HttpStatusCode.Created to { body<MessageResponse>() }
                        HttpStatusCode.OK to {
                            description = "Already in favorites."
                            body<MessageResponse>()
                        }
                        HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                        HttpStatusCode.NotFound to { body<ErrorResponse>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    val principal = call.principal<JWTPrincipal>()!!
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                        ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid prizeId")

                    if (!favoriteRepo.prizeExists(prizeId)) {
                        throw ApiException(HttpStatusCode.NotFound, "Prize $prizeId not found")
                    }
                    when (favoriteRepo.add(principal.userId, prizeId)) {
                        AddResult.Added -> call.respond(
                            HttpStatusCode.Created,
                            MessageResponse("Prize $prizeId added to favorites"),
                        )
                        AddResult.AlreadyExists -> call.respond(
                            HttpStatusCode.OK,
                            MessageResponse("Prize $prizeId is already in favorites"),
                        )
                    }
                }
                delete("/{prizeId}", {
                    tags = listOf("Favorites")
                    summary = "Remove a prize from favorites"
                    securitySchemeNames(SECURITY_SCHEME_JWT)
                    request {
                        pathParameter<Int>("prizeId") { description = "Numeric prize id" }
                    }
                    response {
                        HttpStatusCode.OK to { body<MessageResponse>() }
                        HttpStatusCode.BadRequest to { body<ErrorResponse>() }
                        HttpStatusCode.NotFound to { body<ErrorResponse>() }
                        HttpStatusCode.Unauthorized to { body<ErrorResponse>() }
                    }
                }) {
                    val principal = call.principal<JWTPrincipal>()!!
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                        ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid prizeId")

                    val removed = favoriteRepo.remove(principal.userId, prizeId)
                    if (!removed) {
                        throw ApiException(HttpStatusCode.NotFound, "Favorite for prize $prizeId not found")
                    }
                    call.respond(MessageResponse("Prize $prizeId removed from favorites"))
                }
            }
        }
    }
}

private fun validateCredentials(username: String, password: String) {
    if (username.isBlank() || username.length < 3 || username.length > 100) {
        throw ApiException(HttpStatusCode.BadRequest, "Username must be 3..100 characters")
    }
    if (password.length < 8) {
        throw ApiException(HttpStatusCode.BadRequest, "Password must be at least 8 characters")
    }
}

private fun parseYearCategory(parameters: Parameters): Pair<Int, String> {
    val year = parameters["year"]?.toIntOrNull()
        ?: throw ApiException(HttpStatusCode.BadRequest, "Invalid year")
    val category = parameters["category"]?.lowercase()
        ?: throw ApiException(HttpStatusCode.BadRequest, "Missing category")
    val validCategories = setOf("physics", "chemistry", "medicine", "literature", "peace", "economics")
    if (category !in validCategories) {
        throw ApiException(
            HttpStatusCode.BadRequest,
            "Category must be one of ${validCategories.joinToString(", ")}",
        )
    }
    return year to category
}
