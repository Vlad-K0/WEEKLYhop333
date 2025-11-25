package com.example.weekly.Data.Local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.weekly.Data.Entities.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с таблицей заметок.
 * Здесь определяются все операции CRUD.
 */
@Dao
interface NoteDao {

    // ⭐️ Вставка новой заметки. Если конфликт по ID — заменяем существующую.
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(note: NoteEntity)

    // Обновление существующей заметки
    @Update
    suspend fun update(note: NoteEntity)

    // ⭐️ Новый метод: Вставка или обновление (Upsert)
    // Используется, если note.id != 0, чтобы не создавать дубликаты
    @Upsert
    suspend fun upsert(note: NoteEntity)

    // Удаление заметки
    @Delete
    suspend fun delete(note: NoteEntity)

    // ⭐️ Получение заметки по ID
    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): NoteEntity?

    // Возвращает все заметки, отсортированные по дню, времени начала и содержимому
    // Flow позволяет наблюдать за изменениями в реальном времени
    @Query("SELECT * FROM notes ORDER BY date, start_time, content")
    fun getAllNotes(): Flow<List<NoteEntity>>
}