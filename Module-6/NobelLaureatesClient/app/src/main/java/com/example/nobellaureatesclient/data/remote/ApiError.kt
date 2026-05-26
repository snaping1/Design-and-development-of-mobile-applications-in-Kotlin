package com.example.nobellaureatesclient.data.remote

import com.example.nobellaureatesclient.data.remote.dto.ErrorResponseDto
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import java.io.IOException

class ApiException(
    val status: Int,
    override val message: String,
) : RuntimeException(message)

suspend inline fun <T> runCatchingApi(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (ce: CancellationException) {
    throw ce
} catch (e: ClientRequestException) {
    Result.failure(e.toApiException())
} catch (e: ServerResponseException) {
    Result.failure(e.toApiException())
} catch (e: ResponseException) {
    Result.failure(e.toApiException())
} catch (e: IOException) {
    Result.failure(ApiException(0, "Нет связи с сервером: ${e.message ?: "проверьте сеть"}"))
} catch (t: Throwable) {
    Result.failure(t)
}

suspend fun ResponseException.toApiException(): ApiException {
    val status = response.status.value
    val message = runCatching { response.body<ErrorResponseDto>().error }
        .getOrNull()
        ?: defaultMessage(response.status)
    return ApiException(status, message)
}

private fun defaultMessage(status: HttpStatusCode): String = when (status) {
    HttpStatusCode.Unauthorized -> "Не авторизованы. Войдите снова."
    HttpStatusCode.Forbidden -> "Нет доступа."
    HttpStatusCode.NotFound -> "Не найдено."
    HttpStatusCode.Conflict -> "Конфликт данных."
    HttpStatusCode.BadRequest -> "Некорректный запрос."
    else -> "Ошибка ${status.value}"
}
