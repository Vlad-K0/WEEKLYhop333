package com.example.weekly.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    // ⭐️ ЗАМЕНА insert/update на одну функцию upsert
    suspend fun upsert(note: Note) {
        noteDao.upsert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }
}