package com.example.weekly.Domain.Model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val categoryId: Long
)