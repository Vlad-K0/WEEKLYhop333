package com.example.weekly.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase
import com.example.weekly.Presentation.State.DayListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

/**
 * ViewModel для работы с заметками/делами и настройками темы.
 *
 * ⭐️ Добавлен SettingsManager для управления темой через DataStore
 */
class NoteViewModel(
    private val getOrderedNotesUseCase: GetOrderedNotesUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val toggleDoneStatusUseCase: ToggleDoneStatusUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase
) : ViewModel() {

    // 1. Внутренний MutableStateFlow, который мы меняем
    private val _uiState = MutableStateFlow(DayListUiState())
    
    // 2. Публичный StateFlow, который слушает UI (только для чтения)
    val uiState: StateFlow<DayListUiState> = _uiState.asStateFlow()

    init {
        // Инициализация: загружаем тему, заметки и вычисляем даты
        loadInitialData()
    }

    private fun loadInitialData() {
        // Запускаем корутину для сбора данных
        viewModelScope.launch {
            // Пример объединения потоков
            combine(
                getOrderedNotesUseCase(),
                getThemeUseCase()
            ) { notes, isDark ->
                // Когда приходят новые заметки или меняется тема -> обновляем стейт
                _uiState.value.copy(
                    notes = notes,
                    isDarkTheme = isDark,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
        
        // Инициализируем даты недели
        updateWeekDates(LocalDate.now())
    }

    // Логика переключения недель
    fun onNextWeekClick() {
        val newStart = _uiState.value.currentWeekStart.plusWeeks(1)
        updateWeekDates(newStart)
    }

    fun onPreviousWeekClick() {
        val newStart = _uiState.value.currentWeekStart.minusWeeks(1)
        updateWeekDates(newStart)
    }

    private fun updateWeekDates(startDate: LocalDate) {
        // Логика вычисления дней недели
        val startOfWeek = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = (0L..6L).map { startOfWeek.plusDays(it) }
        
        _uiState.update { it.copy(
            currentWeekStart = startOfWeek,
            weekDates = days
        )}
    }

    // Функция для переключения темы
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            toggleThemeUseCase(isDark)
        }
    }

    // Удаление заметки
    fun deleteNote(note: Note) = viewModelScope.launch {
        deleteUseCase(note)
    }

    // Переключение статуса "выполнено/не выполнено"
    fun toggleDoneStatus(note: Note) = viewModelScope.launch {
        toggleDoneStatusUseCase(note)
    }

    // Сохранение новой или редактирование существующей заметки
    fun saveNote(id: Int, date: String, content: String, startTime: LocalTime?) =
        viewModelScope.launch {
            saveNoteUseCase(id, date, content, startTime)
        }
}
