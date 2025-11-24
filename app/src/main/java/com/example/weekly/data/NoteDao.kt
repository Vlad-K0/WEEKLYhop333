package com.example.weekly.data

import androidx.room.Dao // <-- ОЧЕНЬ ВАЖНО: интерфейс DAO должен быть аннотирован
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с таблицей заметок.
 * Здесь определяются все операции CRUD.
 */
@Dao
interface NoteDao {

    // ⭐️ Вставка новой заметки. Если конфликт по ID — заменяем существующую.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    // Обновление существующей заметки
    @Update
    suspend fun update(note: Note)

    // ⭐️ Новый метод: Вставка или обновление (Upsert)
    // Используется, если note.id != 0, чтобы не создавать дубликаты
    @Upsert
    suspend fun upsert(note: Note)

    // Удаление заметки
    @Delete
    suspend fun delete(note: Note)

    // ⭐️ Получение заметки по ID
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Note?

    // Возвращает все заметки, отсортированные по дню, времени начала и содержимому
    // Flow позволяет наблюдать за изменениями в реальном времени
    @Query("SELECT * FROM notes ORDER BY day, startTime, content")
    fun getAllNotes(): Flow<List<Note>>
}
