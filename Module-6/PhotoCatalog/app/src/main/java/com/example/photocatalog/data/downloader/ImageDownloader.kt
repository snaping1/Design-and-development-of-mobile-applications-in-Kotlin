package com.example.photocatalog.data.downloader

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    suspend fun downloadTo(sourceUrl: String, destinationUri: Uri): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder().url(sourceUrl).build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("HTTP ${response.code} при загрузке изображения")
                    }
                    val body = response.body ?: throw IOException("Пустое тело ответа")

                    val resolver = context.contentResolver
                    resolver.openOutputStream(destinationUri)?.use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    } ?: throw IOException("Не удалось открыть OutputStream для $destinationUri")
                }
                Unit
            }
        }
}