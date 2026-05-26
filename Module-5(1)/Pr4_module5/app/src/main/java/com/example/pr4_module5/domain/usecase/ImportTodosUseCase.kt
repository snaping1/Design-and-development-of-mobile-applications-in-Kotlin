package com.example.pr4_module5.domain.usecase

import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.domain.repository.TodoRepository

class ImportTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(items: List<TodoItem>) = repository.importTodos(items)
    suspend fun getCount(): Int = repository.getCount()
}