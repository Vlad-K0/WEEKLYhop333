package com.example.weekly.Domain.Repository

import com.example.weekly.Domain.Model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeAllNotes(): Flow<List<Note>>

    suspend fun getNoteById(noteId: Long): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(note: Note)
}