package com.example.weekly.data

// data/Note.kt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day: String,
    val content: String,
    // ⭐️ НОВОЕ ПОЛЕ
    val isDone: Boolean = false
)