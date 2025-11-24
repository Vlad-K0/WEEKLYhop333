package com.example.weekly.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ⭐️ КЛАСС ДЛЯ РАБОТЫ С НАСТРОЙКАМИ ПОЛЬЗОВАТЕЛЯ
// SettingsManager инкапсулирует логику чтения и записи данных в DataStore.
// Здесь можно хранить любые параметры (тема, язык, уведомления и т.д.)
class SettingsManager(private val dataStore: DataStore<Preferences>) {

    // 🔑 ВНУТРЕННИЙ ОБЪЕКТ С КЛЮЧАМИ
    // Каждый параметр, который мы храним в DataStore, должен иметь уникальный ключ.
    // booleanPreferencesKey — это типизированный ключ для булевых значений.
    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    // 💡 ПОТОК (Flow), СЛЕДЯЩИЙ ЗА ТЕМ, ВКЛЮЧЕНА ЛИ ТЁМНАЯ ТЕМА
    // `dataStore.data` возвращает поток Preferences — реактивное хранилище всех настроек.
    // Мы мапим его (через .map), чтобы получить только одно нужное значение.
    // Flow обновляется автоматически, если значение изменилось.
    val isDarkTheme: Flow<Boolean> = dataStore.data
        .map { preferences ->
            // Если значение ещё не сохранено — используем false (то есть светлую тему)
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    // 🌓 ФУНКЦИЯ ДЛЯ ИЗМЕНЕНИЯ ТЕМЫ
    // suspend, потому что запись в DataStore — это операция ввода-вывода (I/O)
    // Мы вызываем edit {...}, чтобы безопасно изменить значение.
    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }
}
