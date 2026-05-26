package com.example.pr4_module5.domain.model

data class TodoItem(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)