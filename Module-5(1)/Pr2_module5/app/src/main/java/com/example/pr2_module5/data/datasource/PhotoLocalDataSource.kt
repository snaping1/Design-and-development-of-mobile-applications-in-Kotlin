package com.example.pr2_module5.data.datasource

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.example.pr2_module5.domain.model.PhotoEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoLocalDataSource(private val context: Context) {

    private val photosDir: File
        get() = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir

    fun readAllPhotos(): List<PhotoEntry> {
        return photosDir
            .listFiles { f -> f.extension.lowercase() in listOf("jpg", "jpeg") }
            ?.map { file ->
                PhotoEntry(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    timestamp = file.lastModified()
                )
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    fun createPhotoFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val fileName = "IMG_${timestamp}.jpg"
        return File(photosDir, fileName)
    }

    fun addPhoto(file: File): PhotoEntry {
        return PhotoEntry(
            fileName = file.name,
            filePath = file.absolutePath,
            timestamp = file.lastModified()
        )
    }

    fun exportToGallery(photoEntry: PhotoEntry): Boolean {
        return try {
            val sourceFile = File(photoEntry.filePath)
            if (!sourceFile.exists()) return false

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photoEntry.fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/Pr2Gallery"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return false

            context.contentResolver.openOutputStream(uri)?.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)

            true
        } catch (e: Exception) {
            false
        }
    }
}