package com.example.pr1_module5.data.repository

import com.example.pr1_module5.data.datasource.DiaryLocalDataSource
import com.example.pr1_module5.domain.model.DiaryEntry
import com.example.pr1_module5.domain.repository.DiaryRepository

class DiaryRepositoryImpl(
    private val dataSource: DiaryLocalDataSource
) : DiaryRepository {

    override fun getAllEntries(): List<DiaryEntry> = dataSource.readAllEntries()

    override fun saveEntry(title: String, text: String, existingFileName: String?): DiaryEntry =
        dataSource.writeEntry(title, text, existingFileName)

    override fun deleteEntry(fileName: String) = dataSource.deleteEntry(fileName)

    override fun getEntry(fileName: String): DiaryEntry? = dataSource.readEntry(fileName)
}