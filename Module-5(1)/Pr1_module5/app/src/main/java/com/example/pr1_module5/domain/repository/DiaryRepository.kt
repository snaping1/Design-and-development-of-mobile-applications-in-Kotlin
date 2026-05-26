package com.example.pr1_module5.domain.repository

import com.example.pr1_module5.domain.model.DiaryEntry

interface DiaryRepository {
    fun getAllEntries(): List<DiaryEntry>
    fun saveEntry(title: String, text: String, existingFileName: String? = null): DiaryEntry
    fun deleteEntry(fileName: String)
    fun getEntry(fileName: String): DiaryEntry?
}
