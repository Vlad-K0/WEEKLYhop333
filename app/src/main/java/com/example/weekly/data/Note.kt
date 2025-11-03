package com.example.weekly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    // id = 0 означает новую заметку
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day: String,
    val content: String
)