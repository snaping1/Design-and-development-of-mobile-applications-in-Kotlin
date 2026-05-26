package com.example.pr1_module5.domain.model

data class DiaryEntry(
    val fileName: String,
    val title: String,
    val preview: String,
    val fullText: String,
    val timestamp: Long
)