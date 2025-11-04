package com.example.weekly.data

import androidx.room.Dao // <-- ОЧЕНЬ ВАЖНО
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update // Для функции update

import kotlinx.coroutines.flow.Flow

// ⭐️ КРИТИЧЕСКИ ВАЖНО: Должен быть интерфейс и аннотация @Dao
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: Note) // Изменил insert на upsert, чтобы соответствовать Repository

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes ORDER BY day, isDone ASC, id DESC") // Добавил isDone ASC для сортировки
    fun getAllNotes(): Flow<List<Note>>

    // В Room@Insert(OnConflictStrategy.REPLACE) заменяет insert и update,
    // поэтому отдельная функция update не обязательна, но upsert в DAO должен соответствовать Repository.
}