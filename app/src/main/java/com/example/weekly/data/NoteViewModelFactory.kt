package com.example.weekly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekly.data.settings.SettingsManager

/**
 * Фабрика для создания NoteViewModel с параметрами.
 *
 * ⭐️ Теперь фабрика принимает не только репозиторий, но и SettingsManager,
 * чтобы ViewModel могла работать с настройками темы пользователя через DataStore.
 */
class NoteViewModelFactory(
    private val repository: NoteRepository,        // Репозиторий для работы с заметками/делами
    private val settingsManager: SettingsManager   // Менеджер настроек для темы
) : ViewModelProvider.Factory {

    /**
     * Метод, создающий ViewModel.
     * Проверяем, что запрашиваемая ViewModel — NoteViewModel.
     * Если да, создаем экземпляр, передавая repository и settingsManager.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
