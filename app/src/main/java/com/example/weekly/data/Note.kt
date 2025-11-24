package com.example.weekly.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

/**
 * Сущность заметки для Room.
 * Каждая заметка представляет собой задачу на определённый день.
 */
@Entity(tableName = "notes")
data class Note(
    // Первичный ключ. autoGenerate = true автоматически присваивает уникальный ID.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Дата заметки в формате ISO "yyyy-MM-dd" (например, "2025-11-05")
    val day: String,

    // Содержимое заметки (текст задачи)
    val content: String,

    // Статус выполнения: true — выполнено, false — нет
    val isDone: Boolean = false,

    // ⭐️ НОВОЕ ПОЛЕ: Время начала задачи (может быть null, если не указано)
    val startTime: LocalTime? = null
)
