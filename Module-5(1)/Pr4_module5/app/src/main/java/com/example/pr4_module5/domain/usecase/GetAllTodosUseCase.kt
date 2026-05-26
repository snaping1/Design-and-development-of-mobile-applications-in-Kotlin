package com.example.pr4_module5.domain.usecase

import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetAllTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.getAllTodos()
}