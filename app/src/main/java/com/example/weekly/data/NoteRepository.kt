package com.example.weekly.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    // Возвращает Flow<List<Note>>
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    // ⭐️ Новый метод: Вставка или обновление
    suspend fun upsertNote(note: Note) {
        noteDao.upsert(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun toggleDoneStatus(note: Note) {
        // Логика переключения статуса "выполнено"
        val updatedNote = note.copy(isDone = !note.isDone)
        noteDao.update(updatedNote)
    }

    // ⭐️ Новый метод: Получение заметки по ID
    fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
}