package com.example.weekly.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weekly.Data.Local.Converters
import com.example.weekly.Data.Entities.NoteEntity

/**
 * Основная база данных приложения для хранения заметок.
 * RoomDatabase автоматически создает DAO и управляет сущностями.
 */
@Database(
    entities = [NoteEntity::class], // Сущности базы данных
    version = 3,              // Версия базы данных
    exportSchema = false      // Не сохраняем схему в папку ресурсов
)
// ⭐️ TypeConverters нужны для сохранения нестандартных типов данных (например, LocalTime)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    // DAO для работы с таблицей Note
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        /**
         * Singleton для базы данных, чтобы избежать создания нескольких экземпляров.
         */
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "weekly_database" // Имя файла базы данных
                )
                    // ⚠️ При изменении версии базы данных:
                    // .fallbackToDestructiveMigration() может быть полезно на этапе разработки
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}