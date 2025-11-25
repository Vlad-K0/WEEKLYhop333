package com.example.weekly.Data.Local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weekly.Data.Entities.GroupEntity
import com.example.weekly.Data.Entities.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultGroupsCallback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Добавляем дефолтные группы при первом создании БД
        db.execSQL("INSERT INTO groups (name, color) VALUES ('Работа', '#FF5722')")
        db.execSQL("INSERT INTO groups (name, color) VALUES ('Личное', '#4CAF50')")
        db.execSQL("INSERT INTO groups (name, color) VALUES ('Здоровье', '#2196F3')")
    }
}
