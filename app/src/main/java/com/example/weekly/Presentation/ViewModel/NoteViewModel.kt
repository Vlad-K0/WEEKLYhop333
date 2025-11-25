package com.example.weekly.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weekly.Data.Settings.SettingsManager
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

/**
 * ViewModel для работы с заметками/делами и настройками темы.
 *
 * ⭐️ Добавлен SettingsManager для управления темой через DataStore
 */
class NoteViewModel(
    getOrderedNotesUseCase: GetOrderedNotesUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val toggleDoneStatusUseCase: ToggleDoneStatusUseCase,
    private val settingsManager: SettingsManager    // Менеджер настроек для работы с темой
) : ViewModel() {

    // ⭐️ StateFlow для наблюдения за темой (темная/светлая)
    val isDarkTheme: StateFlow<Boolean> = settingsManager.isDarkTheme.stateIn(
        scope = viewModelScope,                     // Используем viewModelScope для корректного lifecycle
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false                        // По умолчанию светлая тема
    )

    // ⭐️ StateFlow для заметок, сгруппированных по дню и отсортированных
    val notesGroupedByDay: StateFlow<Map<String, List<Note>>> = getOrderedNotesUseCase()
        .stateIn(
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope,
            initialValue = emptyMap()
        )


    // ⭐️ Функция для переключения темы
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsManager.toggleTheme(isDark)
        }
    }

    // ⭐️ Удаление заметки
    fun deleteNote(note: Note) = viewModelScope.launch {
        deleteUseCase(note)
    }

    // ⭐️ Переключение статуса "выполнено/не выполнено"
    fun toggleDoneStatus(note: Note) = viewModelScope.launch {
        toggleDoneStatusUseCase(note)
    }

    // ⭐️ Сохранение новой или редактирование существующей заметки
    fun saveNote(id: Int, date: String, content: String, startTime: LocalTime?) =
        viewModelScope.launch {
            saveNoteUseCase(id, date, content, startTime)
        }
}
