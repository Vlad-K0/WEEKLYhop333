package com.example.weekly.Presentation.State

import com.example.weekly.Domain.Model.Note
import java.time.LocalDate

data class DayListUiState(
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = false,
    
    // Данные для отображения списка
    val currentWeekStart: LocalDate = LocalDate.now(), // Начало текущей недели
    val weekDates: List<LocalDate> = emptyList(),      // Список дат недели
    val notes: Map<String, List<Note>> = emptyMap(),   // Заметки (ключ - дата ISO)
    
    val error: String? = null
)
