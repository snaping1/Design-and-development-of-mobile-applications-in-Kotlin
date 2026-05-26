package com.example.pr1_module5.domain.usecase

import com.example.pr1_module5.domain.repository.DiaryRepository

class DeleteEntryUseCase(private val repository: DiaryRepository) {
    operator fun invoke(fileName: String) = repository.deleteEntry(fileName)
}
