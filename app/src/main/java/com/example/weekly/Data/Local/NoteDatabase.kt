package com.example.weekly.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weekly.Data.Local.Converters
import com.example.weekly.Data.Entities.GroupEntity
import com.example.weekly.Data.Entities.NoteEntity

/**
 * Основная база данных приложения для хранения заметок и групп.
 * RoomDatabase автоматически создает DAO и управляет сущностями.
 */
@Database(
    entities = [NoteEntity::class, GroupEntity::class], // Сущности базы данных
    version = 4,              // Версия базы данных (увеличена для добавления GroupEntity)
    exportSchema = false      // Не сохраняем схему в папку ресурсов
)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    // DAO для работы с таблицей Note
    abstract fun noteDao(): NoteDao
    
    // DAO для работы с таблицей Group
    abstract fun groupDao(): GroupDao

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
                    .fallbackToDestructiveMigration() // Удаляет и пересоздает БД при миграции (только для разработки!)
                    .addCallback(defaultGroupsCallback) // Добавляем дефолтные группы
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}