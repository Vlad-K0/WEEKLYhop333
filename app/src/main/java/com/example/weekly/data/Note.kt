package com.example.weekly.data

// data/Note.kt

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day: String, // Дата в формате ISO: "yyyy-MM-dd"
    val content: String,
    val isDone: Boolean = false,
    // ⭐️ НОВОЕ ПОЛЕ: Время начала (опционально)
    val startTime: LocalTime? = null
)