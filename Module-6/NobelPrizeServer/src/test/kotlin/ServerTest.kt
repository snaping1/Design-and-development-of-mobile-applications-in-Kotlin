package com.example

import com.example.model.AuthResponse
import com.example.model.MessageResponse
import com.example.model.PrizeResponse
import com.example.model.PrizeWithLaureatesResponse
import com.example.model.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServerTest {

    private fun ApplicationTestBuilder.useTestConfig() {
        environment { config = ApplicationConfig("application-test.yaml") }
    }

    @Test
    fun `root endpoint returns OK without auth`() = testApplication {
        useTestConfig()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `prizes endpoint requires authentication`() = testApplication {
        useTestConfig()
        val response = client.get("/prizes")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `register login and access protected endpoint`() = testApplication {
        useTestConfig()
        val httpClient = createClient { install(ContentNegotiation) { json() } }

        val username = "tester_${System.currentTimeMillis()}"
        val registerResp = httpClient.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, "supersecret"))
        }
        assertEquals(HttpStatusCode.Created, registerResp.status)
        val auth: AuthResponse = registerResp.body()
        assertNotNull(auth.token)
        assertEquals(username, auth.username)

        val prizesResp = httpClient.get("/prizes") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.OK, prizesResp.status)
        val prizes: List<PrizeResponse> = prizesResp.body()
        assertTrue(prizes.isNotEmpty(), "Seeded prize list should not be empty")
    }

    @Test
    fun `fetch specific prize with laureates`() = testApplication {
        useTestConfig()
        val httpClient = createClient { install(ContentNegotiation) { json() } }

        val auth: AuthResponse = httpClient.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("user_${System.nanoTime()}", "supersecret"))
        }.body()

        val prizeResp = httpClient.get("/prizes/1921/physics") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.OK, prizeResp.status)
        val prize: PrizeWithLaureatesResponse = prizeResp.body()
        assertEquals(1921, prize.year)
        assertEquals("physics", prize.category)
        assertTrue(prize.laureates.any { it.lastName == "Einstein" })
    }

    @Test
    fun `add and remove favorite`() = testApplication {
        useTestConfig()
        val httpClient = createClient { install(ContentNegotiation) { json() } }

        val auth: AuthResponse = httpClient.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("favuser_${System.nanoTime()}", "supersecret"))
        }.body()

        val prizes: List<PrizeResponse> = httpClient.get("/prizes") { bearerAuth(auth.token) }.body()
        val prizeId = prizes.first().id

        val addResp = httpClient.post("/users/me/favorites/$prizeId") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.Created, addResp.status)

        val readdResp = httpClient.post("/users/me/favorites/$prizeId") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.OK, readdResp.status)
        assertTrue(readdResp.body<MessageResponse>().message.contains("already"))

        val listResp = httpClient.get("/users/me/favorites") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.OK, listResp.status)

        val deleteResp = httpClient.delete("/users/me/favorites/$prizeId") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.OK, deleteResp.status)

        val deleteAgainResp = httpClient.delete("/users/me/favorites/$prizeId") { bearerAuth(auth.token) }
        assertEquals(HttpStatusCode.NotFound, deleteAgainResp.status)
    }
}
