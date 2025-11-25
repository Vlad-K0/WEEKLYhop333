package com.example.weekly.Domain.Model

import java.time.LocalTime

data class Note(
    val id: Int,
    val content: String,
    val date: String,
    val isDone: Boolean,
    val startTime: LocalTime?,
    val groupId: Int? = null // Nullable - tasks can be ungrouped
)