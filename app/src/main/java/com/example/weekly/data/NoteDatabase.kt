package com.example.weekly.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // ⭐️ ОБЯЗАТЕЛЬНЫЙ ИМПОРТ

// Обновленный класс NoteDatabase
@Database(entities = [Note::class], version = 3, exportSchema = false)
// ⭐️ ДОБАВЬТЕ ЭТУ АННОТАЦИЮ!
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, NoteDatabase::class.java, "weekly_database")
                    .build()
                    // ❗️ ВАЖНО: При первой установке новой версии (version = 2)
                    // нужно удалить старую базу или добавить .fallbackToDestructiveMigration()
                    // чтобы избежать ошибки миграции.
                    .also { Instance = it }
            }
        }
    }
}