package com.example.pr1_module5.data.datasource

import android.content.Context
import com.example.pr1_module5.domain.model.DiaryEntry
import java.io.File

class DiaryLocalDataSource(private val context: Context) {

    private val dir: File get() = context.filesDir

    fun readAllEntries(): List<DiaryEntry> {
        return dir.listFiles { f -> f.extension == "txt" }
            ?.mapNotNull { parseFile(it) }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    fun writeEntry(title: String, text: String, existingFileName: String? = null): DiaryEntry {
        val timestamp: Long
        val fileName: String

        if (existingFileName != null) {
            fileName = existingFileName
            timestamp = existingFileName.split("_").firstOrNull()?.toLongOrNull()
                ?: System.currentTimeMillis()
        } else {
            timestamp = System.currentTimeMillis()
            val safeName = title
                .take(30)
                .replace(Regex("[^a-zA-Zа-яА-Я0-9_]"), "_")
                .ifBlank { "note" }
            fileName = "${timestamp}_${safeName}.txt"
        }

        val file = File(dir, fileName)
        file.writeText(buildFileContent(title, text, timestamp), Charsets.UTF_8)

        return DiaryEntry(
            fileName = fileName,
            title = title.ifBlank { "Без заголовка" },
            preview = text.take(40),
            fullText = text,
            timestamp = timestamp
        )
    }

    fun deleteEntry(fileName: String) {
        File(dir, fileName).delete()
    }

    fun readEntry(fileName: String): DiaryEntry? {
        val file = File(dir, fileName)
        if (!file.exists()) return null
        return parseFile(file)
    }


    private fun buildFileContent(title: String, text: String, timestamp: Long): String {
        return "TITLE:${title}\nTIMESTAMP:${timestamp}\n---\n$text"
    }

    private fun parseFile(file: File): DiaryEntry? {
        return try {
            val lines = file.readLines(Charsets.UTF_8)
            val title = lines.firstOrNull { it.startsWith("TITLE:") }
                ?.removePrefix("TITLE:") ?: "Без заголовка"
            val timestamp = lines.firstOrNull { it.startsWith("TIMESTAMP:") }
                ?.removePrefix("TIMESTAMP:")?.toLongOrNull()
                ?: file.lastModified()
            val separatorIndex = lines.indexOfFirst { it == "---" }
            val fullText = if (separatorIndex >= 0)
                lines.drop(separatorIndex + 1).joinToString("\n")
            else ""
            DiaryEntry(
                fileName = file.name,
                title = title.ifBlank { "Без заголовка" },
                preview = fullText.take(40),
                fullText = fullText,
                timestamp = timestamp
            )
        } catch (e: Exception) {
            null
        }
    }
}