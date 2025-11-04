package com.example.weekly.data

import androidx.room.Dao // <-- ОЧЕНЬ ВАЖНО
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update // Для функции update
import androidx.room.Upsert

import kotlinx.coroutines.flow.Flow

// ⭐️ КРИТИЧЕСКИ ВАЖНО: Должен быть интерфейс и аннотация @Dao
@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    // ⭐️ Новый метод для вставки или обновления (если note.id != 0)
    @Upsert
    suspend fun upsert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // ⭐️ Новый метод для получения заметки по ID
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Note?

    // Обновлен: теперь возвращает Flow<List<Note>>
    @Query("SELECT * FROM notes ORDER BY day, startTime, content")
    fun getAllNotes(): Flow<List<Note>>
}