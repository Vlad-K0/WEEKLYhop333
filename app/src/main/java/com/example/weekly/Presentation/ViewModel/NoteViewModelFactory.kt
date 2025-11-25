package com.example.weekly.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase

/**
 * Фабрика для создания NoteViewModel с параметрами.
 *
 * ⭐️ Теперь фабрика принимает не только репозиторий, но и SettingsManager,
 * чтобы ViewModel могла работать с настройками темы пользователя через DataStore.
 */
class NoteViewModelFactory(
    private val getOrderedNotesUseCase: GetOrderedNotesUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val toggleDoneStatusUseCase: ToggleDoneStatusUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase
) : ViewModelProvider.Factory {

    /**
     * Метод, создающий ViewModel.
     * Проверяем, что запрашиваемая ViewModel — NoteViewModel.
     * Если да, создаем экземпляр, передавая repository и settingsManager.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                getOrderedNotesUseCase,
                getThemeUseCase,
                deleteUseCase,
                saveNoteUseCase,
                toggleDoneStatusUseCase,
                toggleThemeUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
