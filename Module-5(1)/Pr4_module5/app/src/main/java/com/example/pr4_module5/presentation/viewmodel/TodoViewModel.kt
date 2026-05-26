package com.example.pr4_module5.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pr4_module5.data.local.TodoDatabase
import com.example.pr4_module5.data.preferences.ThemePreferences
import com.example.pr4_module5.data.repository.TodoRepositoryImpl
import com.example.pr4_module5.domain.model.TodoItem
import com.example.pr4_module5.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TodoDatabase.getDatabase(application).todoDao()
    private val repository = TodoRepositoryImpl(dao)
    private val themePreferences = ThemePreferences(application)

    private val getAllTodos = GetAllTodosUseCase(repository)
    private val addTodo = AddTodoUseCase(repository)
    private val updateTodo = UpdateTodoUseCase(repository)
    private val deleteTodo = DeleteTodoUseCase(repository)
    private val importTodos = ImportTodosUseCase(repository)

    val todos: StateFlow<List<TodoItem>> = getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val highlightCompleted: StateFlow<Boolean> = themePreferences.highlightCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            if (importTodos.getCount() == 0) {
                importFromJson()
            }
        }
    }

    private suspend fun importFromJson() {
        val jsonTodos = listOf(
            TodoItem(title = "Купить продукты", description = "Молоко, хлеб, яйца"),
            TodoItem(title = "Позвонить врачу", description = "Записаться на приём"),
            TodoItem(title = "Сделать зарядку", description = "30 минут утром"),
            TodoItem(title = "Прочитать книгу", description = "Глава 5-7"),
            TodoItem(title = "Оплатить счета", description = "Интернет и телефон")
        )
        importTodos(jsonTodos)
    }

    fun addTodo(title: String, description: String) {
        viewModelScope.launch {
            addTodo(TodoItem(title = title, description = description))
        }
    }

    fun toggleCompleted(item: TodoItem) {
        viewModelScope.launch {
            updateTodo(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun updateTodo(item: TodoItem) {
        viewModelScope.launch {
            updateTodo.invoke(item)
        }
    }

    fun deleteTodo(item: TodoItem) {
        viewModelScope.launch {
            deleteTodo.invoke(item)
        }
    }

    fun setHighlightCompleted(value: Boolean) {
        viewModelScope.launch {
            themePreferences.setHighlightCompleted(value)
        }
    }
}