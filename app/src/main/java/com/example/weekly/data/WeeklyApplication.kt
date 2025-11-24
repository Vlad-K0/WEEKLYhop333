package com.example.weekly.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore // <-- НОВЫЙ ИМПОРТ

import com.example.weekly.data.settings.SettingsManager // <-- НОВЫЙ ИМПОРТ


// ⭐️ 1. ГЛОБАЛЬНАЯ ИНИЦИАЛИЗАЦИЯ DATASTORE
// Эта строка создаёт DataStore (аналог SharedPreferences, но современный и реактивный).
// Он хранит данные в XML-файле с именем "settings".
// Благодаря `by preferencesDataStore`, каждый Context в приложении получает
// свойство `dataStore`, к которому можно безопасно обращаться из любого места.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


// ⭐️ 2. КЛАСС APPLICATION
// Этот класс живёт столько же, сколько и всё приложение.
// Здесь создаются и хранятся все глобальные синглтоны: база данных, репозиторий, DataStore и т.п.
class WeeklyApplication : Application() {

    // ⚙️ ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ
    // NoteDatabase — это Room-база данных, создающаяся при первом обращении.
    // `.getDatabase(this)` гарантирует, что будет один общий экземпляр для всего приложения.
    val database by lazy { NoteDatabase.getDatabase(this) }

    // 📦 ИНИЦИАЛИЗАЦИЯ РЕПОЗИТОРИЯ
    // Репозиторий — слой, через который ViewModel работает с БД.
    // В него передаётся DAO (data access object) из базы.
    val repository by lazy { NoteRepository(database.noteDao()) }

    // 🌙 ИНИЦИАЛИЗАЦИЯ DATASTORE ЧЕРЕЗ SettingsManager
    // SettingsManager — отдельный класс, который управляет настройками приложения
    // (например, светлая/тёмная тема, будущие параметры и т.д.)
    private val _settingsManager by lazy { SettingsManager(dataStore) }

    // 🧩 Публичный геттер для ViewModel или Activity,
    // чтобы можно было получить доступ к SettingsManager из любого места.
    val settingsManager get() = _settingsManager
}
