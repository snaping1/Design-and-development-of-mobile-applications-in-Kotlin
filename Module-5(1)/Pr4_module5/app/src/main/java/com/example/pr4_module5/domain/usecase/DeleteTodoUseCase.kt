package com.example.pr4_module5.domain.usecase

import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.domain.repository.TodoRepository

class DeleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(item: TodoItem) = repository.deleteTodo(item)
}