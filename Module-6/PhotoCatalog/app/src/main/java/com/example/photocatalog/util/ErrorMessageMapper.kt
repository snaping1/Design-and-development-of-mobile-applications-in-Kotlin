package com.example.photocatalog.util

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Нет соединения с сетью. Проверьте интернет."
    is HttpException -> "Ошибка сервера: ${code()}"
    else -> message ?: "Неизвестная ошибка"
}