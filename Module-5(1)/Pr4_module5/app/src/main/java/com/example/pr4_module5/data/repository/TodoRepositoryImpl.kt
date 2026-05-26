package com.example.pr4_module5.data.repository

import com.example.pr4_module5.data.local.TodoDao
import com.example.pr4_module5.data.model.TodoEntity
import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {

    override fun getAllTodos(): Flow<List<TodoItem>> =
        dao.getAllTodos().map { list -> list.map { it.toDomain() } }

    override suspend fun addTodo(item: TodoItem) =
        dao.insertTodo(item.toEntity())

    override suspend fun updateTodo(item: TodoItem) =
        dao.updateTodo(item.toEntity())

    override suspend fun deleteTodo(item: TodoItem) =
        dao.deleteTodo(item.toEntity())

    override suspend fun importTodos(items: List<TodoItem>) =
        dao.insertAll(items.map { it.toEntity() })

    override suspend fun getCount(): Int = dao.getCount()

    private fun TodoEntity.toDomain() = TodoItem(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted
    )

    private fun TodoItem.toEntity() = TodoEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted
    )
}