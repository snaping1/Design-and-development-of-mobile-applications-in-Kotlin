package com.example.pr1_module5.domain.usecase

import com.example.pr1_module5.domain.model.DiaryEntry
import com.example.pr1_module5.domain.repository.DiaryRepository

class GetAllEntriesUseCase(private val repository: DiaryRepository) {
    operator fun invoke(): List<DiaryEntry> = repository.getAllEntries()
}