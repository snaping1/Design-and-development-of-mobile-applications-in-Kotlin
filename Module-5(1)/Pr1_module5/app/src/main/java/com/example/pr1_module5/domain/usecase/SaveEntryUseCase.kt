package com.example.pr1_module5.domain.usecase

import com.example.pr1_module5.domain.model.DiaryEntry
import com.example.pr1_module5.domain.repository.DiaryRepository

class SaveEntryUseCase(private val repository: DiaryRepository) {
    operator fun invoke(
        title: String,
        text: String,
        existingFileName: String? = null
    ): DiaryEntry = repository.saveEntry(title, text, existingFileName)
}
