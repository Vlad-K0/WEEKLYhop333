package com.example.weekly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weekly.data.settings.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

/**
 * ViewModel для работы с заметками/делами и настройками темы.
 *
 * ⭐️ Добавлен SettingsManager для управления темой через DataStore
 */
class NoteViewModel(
    private val repository: NoteRepository,         // Репозиторий для CRUD операций с заметками
    private val settingsManager: SettingsManager    // Менеджер настроек для работы с темой
) : ViewModel() {

    // ⭐️ StateFlow для наблюдения за темой (темная/светлая)
    val isDarkTheme: StateFlow<Boolean> = settingsManager.isDarkTheme.stateIn(
        scope = viewModelScope,                     // Используем viewModelScope для корректного lifecycle
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false                        // По умолчанию светлая тема
    )

    // ⭐️ Функция для переключения темы
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsManager.toggleTheme(isDark)
        }
    }

    // ⭐️ StateFlow для заметок, сгруппированных по дню и отсортированных
    val notesGroupedByDay: StateFlow<Map<String, List<Note>>> = repository.getAllNotes()
        .map { notes: List<Note> ->
            notes
                .groupBy { it.day }  // Группируем по дню
                .mapValues { (_, dayNotes: List<Note>) ->
                    // Сортировка: сначала по статусу isDone, затем по наличию startTime, потом по времени
                    dayNotes.sortedWith(
                        compareBy<Note> { it.isDone }
                            .thenBy { it.startTime == null }
                            .thenBy { it.startTime }
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()  // Изначально пустой список
        )

    // ⭐️ Удаление заметки
    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    // ⭐️ Переключение статуса "выполнено/не выполнено"
    fun toggleDoneStatus(note: Note) = viewModelScope.launch {
        repository.toggleDoneStatus(note)
    }

    // ⭐️ Сохранение новой или редактирование существующей заметки
    fun saveNote(id: Int, day: String, content: String, startTime: LocalTime?) = viewModelScope.launch {
        val existingNote = if (id != 0) repository.getNoteById(id) else null

        val note = Note(
            id = if (id == 0) 0 else id,
            day = day,
            content = content,
            isDone = existingNote?.isDone ?: false,
            startTime = startTime
        )
        repository.upsertNote(note) // Добавляем или обновляем заметку
    }
}
