package com.example.pr4_module5.domain.repository

import com.example.pr4_module5.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(item: TodoItem)
    suspend fun updateTodo(item: TodoItem)
    suspend fun deleteTodo(item: TodoItem)
    suspend fun importTodos(items: List<TodoItem>)
    suspend fun getCount(): Int
}