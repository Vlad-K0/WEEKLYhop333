package com.example.weekly

import android.app.Application
import com.example.weekly.data.NoteDatabase
import com.example.weekly.data.NoteRepository

class WeeklyApplication : Application() {
    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}