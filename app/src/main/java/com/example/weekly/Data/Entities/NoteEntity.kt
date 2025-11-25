package com.example.weekly.Data.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

/**
 * Сущность заметки для Room.
 * Каждая заметка представляет собой задачу на определённый день.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    // Первичный ключ. autoGenerate = true автоматически присваивает уникальный ID.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Дата заметки в формате ISO "yyyy-MM-dd" (например, "2025-11-05")
    @ColumnInfo(name = "date")
    val date: String,

    // Содержимое заметки (текст задачи)
    @ColumnInfo(name = "content")
    val content: String,

    // Статус выполнения: true — выполнено, false — нет
    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    // ⭐️ НОВОЕ ПОЛЕ: Время начала задачи (может быть null, если не указано)
    @ColumnInfo(name = "start_time")
    val startTime: LocalTime? = null
)