package com.example.weekly.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с заметками.
 * Содержит методы для получения, вставки, обновления и удаления заметок.
 */

 // Repository != DAO это разные уровни абстракии DAO
@Singleton
class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    // ⭐️ Возвращает поток всех заметок из базы данных
    // Можно подписываться в ViewModel через StateFlow/LiveData
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    // ⭐️ Вставка новой заметки или обновление существующей
    // Если заметка уже существует (по ID), обновляется
    suspend fun upsertNote(note: Note) {
        noteDao.upsert(note)
    }

    // ⭐️ Удаление заметки
    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    // ⭐️ Переключение статуса "выполнено/не выполнено"
    suspend fun toggleDoneStatus(note: Note) {
        val updatedNote = note.copy(isDone = !note.isDone)
        noteDao.update(updatedNote)
    }

    // ⭐️ Получение заметки по ID (для редактирования)
    fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
}
