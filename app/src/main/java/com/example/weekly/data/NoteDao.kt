package com.example.weekly.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // Получение всех заметок
    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotes(): Flow<List<Note>>

    // Вставка/Обновление. REPLACE позволяет обновить заметку, если id уже существует
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    // Удаление заметки
    @Delete
    suspend fun delete(note: Note)
}