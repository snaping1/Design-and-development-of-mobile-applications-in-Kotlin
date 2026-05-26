package com.example.pr1_module5.domain.usecase

import com.example.pr1_module5.domain.model.DiaryEntry
import com.example.pr1_module5.domain.repository.DiaryRepository

class GetEntryUseCase(private val repository: DiaryRepository) {
    operator fun invoke(fileName: String): DiaryEntry? = repository.getEntry(fileName)
}
